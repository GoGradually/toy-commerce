package me.gogradually.toycommerce.application.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.command.CompleteOrderDetailsCommand;
import me.gogradually.toycommerce.application.order.command.PayOrderCommand;
import me.gogradually.toycommerce.application.order.dto.CheckoutOrderInfo;
import me.gogradually.toycommerce.application.order.dto.CompleteOrderDetailsInfo;
import me.gogradually.toycommerce.application.order.dto.OrderDetailInfo;
import me.gogradually.toycommerce.application.order.dto.PayOrderInfo;
import me.gogradually.toycommerce.application.order.event.OrderCreatedEvent;
import me.gogradually.toycommerce.application.order.event.OrderInfoCompletedEvent;
import me.gogradually.toycommerce.application.order.event.OrderPaymentFailedEvent;
import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
import me.gogradually.toycommerce.domain.cart.Cart;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import me.gogradually.toycommerce.domain.order.*;
import me.gogradually.toycommerce.domain.order.exception.*;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final int PAYMENT_TIMEOUT_MAX_ATTEMPTS = 2;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public CheckoutOrderInfo checkout(Long memberId) {
        validateMemberId(memberId);

        Cart cart = Cart.of(memberId, cartRepository.findByMemberId(memberId));
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(memberId);
        }

        List<CartItem> sortedCartItems = sortCartItemsByProductId(cartItems);
        Map<Long, Product> lockedProducts = getLockedProducts(sortedCartItems);

        List<OrderItem> orderItems = makeOrderItems(cartItems, lockedProducts);

        Order savedOrder = orderRepository.save(Order.checkout(memberId, orderItems));
        applicationEventPublisher.publishEvent(OrderCreatedEvent.from(savedOrder));
        cartRepository.deleteByMemberId(memberId);

        return CheckoutOrderInfo.from(savedOrder);
    }

    @Transactional
    public CompleteOrderDetailsInfo completeOrderDetails(Long memberId, Long orderId, CompleteOrderDetailsCommand command) {
        validateMemberId(memberId);

        Order order = getOrderForUpdateWithOwnership(memberId, orderId);
        OrderDetails details = command.toOrderDetails();
        order.completeDetails(details);

        Order saved = orderRepository.save(order);
        applicationEventPublisher.publishEvent(OrderInfoCompletedEvent.from(saved));
        return CompleteOrderDetailsInfo.from(saved);
    }

    @Transactional(noRollbackFor = PaymentFailedException.class)
    public PayOrderInfo pay(Long memberId, Long orderId, PayOrderCommand command) {
        validateMemberId(memberId);

        Order order = getOrderForUpdateWithOwnership(memberId, orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            return PayOrderInfo.success(order);
        }

        if (order.getStatus() != OrderStatus.INFO_COMPLETED) {
            throw new InvalidOrderStateException(order.getStatus(), OrderStatus.INFO_COMPLETED, OrderStatus.PAID);
        }

        boolean paid = requestPaymentWithRetry(order, command.paymentToken());
        if (!paid) {
            order.markPaymentFailed();
            Order savedFailedOrder = orderRepository.save(order);
            applicationEventPublisher.publishEvent(OrderPaymentFailedEvent.from(savedFailedOrder));
            throw new PaymentFailedException(orderId);
        }

        order.markPaid();
        Order saved = orderRepository.save(order);
        return PayOrderInfo.success(saved);
    }

    public OrderDetailInfo getOrder(Long memberId, Long orderId) {
        validateMemberId(memberId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getMemberId().equals(memberId)) {
            throw new OrderNotFoundException(orderId);
        }

        return OrderDetailInfo.from(order);
    }

    private boolean requestPaymentWithRetry(Order order, String paymentToken) {
        for (int attempt = 1; attempt <= PAYMENT_TIMEOUT_MAX_ATTEMPTS; attempt++) {
            try {
                return paymentGateway.pay(
                        order.getId(),
                        order.getMemberId(),
                        order.getTotalAmount(),
                        paymentToken
                ).success();
            } catch (PaymentTimeoutException ignored) {
                if (attempt == PAYMENT_TIMEOUT_MAX_ATTEMPTS) {
                    return false;
                }
            }
        }

        return false;
    }

    private Map<Long, Product> getLockedProducts(List<CartItem> sortedCartItems) {
        Map<Long, Product> lockedProducts = new HashMap<>();
        for (CartItem cartItem : sortedCartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(cartItem.getProductId()));

            ensureProductIsActive(product);
            lockedProducts.put(product.getId(), product);
        }
        return lockedProducts;
    }

    private List<OrderItem> makeOrderItems(List<CartItem> cartItems, Map<Long, Product> lockedProducts) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = lockedProducts.get(cartItem.getProductId());
            if (product == null) {
                throw new ProductNotFoundException(cartItem.getProductId());
            }
            orderItems.add(OrderItem.create(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity()
            ));
        }
        return orderItems;
    }

    private Order getOrderForUpdateWithOwnership(Long memberId, Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getMemberId().equals(memberId)) {
            throw new OrderNotFoundException(orderId);
        }

        return order;
    }

    private void ensureProductIsActive(Product product) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new InactiveCartProductException(product.getId(), product.getStatus());
        }
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new InvalidOrderMemberIdException(memberId);
        }
    }

    private List<CartItem> sortCartItemsByProductId(List<CartItem> cartItems) {
        return cartItems.stream()
                .sorted(java.util.Comparator.comparing(CartItem::getProductId))
                .toList();
    }
}

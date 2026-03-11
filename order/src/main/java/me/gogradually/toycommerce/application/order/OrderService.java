package me.gogradually.toycommerce.application.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.command.CompleteOrderDetailsCommand;
import me.gogradually.toycommerce.application.order.command.PayOrderCommand;
import me.gogradually.toycommerce.application.order.dto.*;
import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.application.order.event.OrderCreatedEvent;
import me.gogradually.toycommerce.application.order.event.OrderInfoCompletedEvent;
import me.gogradually.toycommerce.application.order.event.OrderLineSnapshot;
import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
import me.gogradually.toycommerce.application.order.port.ProductSnapshot;
import me.gogradually.toycommerce.application.order.port.ProductSnapshotPort;
import me.gogradually.toycommerce.domain.cart.Cart;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import me.gogradually.toycommerce.domain.order.*;
import me.gogradually.toycommerce.domain.order.exception.*;
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
    private final ProductSnapshotPort productSnapshotPort;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public CheckoutOrderResult checkout(Long memberId) {
        validateMemberId(memberId);

        return orderRepository.findLatestOpenOrder(memberId)
                .map(order -> CheckoutOrderResult.reused(CheckoutOrderInfo.from(order)))
                .orElseGet(() -> createCheckoutOrder(memberId));
    }

    @Transactional
    public CancelOrderInfo cancel(Long memberId, Long orderId) {
        validateMemberId(memberId);

        Order order = getOrderForUpdateWithOwnership(memberId, orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return CancelOrderInfo.from(order);
        }

        order.cancel();
        Order saved = orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderCancelledEvent(saved.getId(), toSnapshots(saved.getItems())));
        return CancelOrderInfo.from(saved);
    }

    private CheckoutOrderResult createCheckoutOrder(Long memberId) {
        validateMemberId(memberId);

        Cart cart = Cart.of(memberId, cartRepository.findByMemberId(memberId));
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(memberId);
        }

        List<CartItem> sortedCartItems = sortCartItemsByProductId(cartItems);
        Map<Long, ProductSnapshot> lockedProducts = getLockedProducts(sortedCartItems);

        List<OrderItem> orderItems = makeOrderItems(cartItems, lockedProducts);

        Order savedOrder = orderRepository.save(Order.checkout(memberId, orderItems));
        applicationEventPublisher.publishEvent(new OrderCreatedEvent(savedOrder.getId(), toSnapshots(savedOrder.getItems())));

        return CheckoutOrderResult.created(CheckoutOrderInfo.from(savedOrder));
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

    @Transactional
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
            Order replacementOrder = orderRepository.save(savedFailedOrder.recreateForRetry());
            return PayOrderInfo.failed(savedFailedOrder, replacementOrder.getId());
        }

        order.markPaid();
        Order saved = orderRepository.save(order);
        cleanupCartForPaidOrder(saved);
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

    private Map<Long, ProductSnapshot> getLockedProducts(List<CartItem> sortedCartItems) {
        Map<Long, ProductSnapshot> lockedProducts = new HashMap<>();
        for (CartItem cartItem : sortedCartItems) {
            ProductSnapshot product = productSnapshotPort.getActiveProduct(cartItem.getProductId());
            lockedProducts.put(product.productId(), product);
        }
        return lockedProducts;
    }

    private List<OrderItem> makeOrderItems(List<CartItem> cartItems, Map<Long, ProductSnapshot> lockedProducts) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ProductSnapshot product = lockedProducts.get(cartItem.getProductId());
            if (product == null) {
                throw new OrderProductNotFoundException(cartItem.getProductId());
            }
            orderItems.add(OrderItem.create(
                    product.productId(),
                    product.name(),
                    product.price(),
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

    private void cleanupCartForPaidOrder(Order order) {
        for (OrderItem orderItem : order.getItems()) {
            cartRepository.findByMemberIdAndProductId(order.getMemberId(), orderItem.getProductId())
                    .ifPresent(cartItem -> adjustCartItem(order, orderItem, cartItem));
        }
    }

    private void adjustCartItem(Order order, OrderItem orderItem, CartItem cartItem) {
        if (cartItem.getQuantity() > orderItem.getQuantity()) {
            cartItem.changeQuantity(cartItem.getQuantity() - orderItem.getQuantity());
            cartRepository.save(cartItem);
            return;
        }

        cartRepository.deleteByMemberIdAndProductId(order.getMemberId(), orderItem.getProductId());
    }

    private List<OrderLineSnapshot> toSnapshots(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderLineSnapshot(item.getProductId(), item.getQuantity()))
                .toList();
    }
}

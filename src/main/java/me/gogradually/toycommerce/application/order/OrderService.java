package me.gogradually.toycommerce.application.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.command.PayOrderCommand;
import me.gogradually.toycommerce.application.order.dto.CheckoutOrderInfo;
import me.gogradually.toycommerce.application.order.dto.OrderDetailInfo;
import me.gogradually.toycommerce.application.order.dto.PayOrderInfo;
import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
import me.gogradually.toycommerce.domain.cart.Cart;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderItem;
import me.gogradually.toycommerce.domain.order.OrderRepository;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import me.gogradually.toycommerce.domain.order.exception.*;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final int PAYMENT_TIMEOUT_MAX_ATTEMPTS = 2;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;

    @Transactional
    public CheckoutOrderInfo checkout(Long memberId) {
        validateMemberId(memberId);

        Cart cart = Cart.of(memberId, cartRepository.findByMemberId(memberId));
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(memberId);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(cartItem.getProductId()));

            ensureProductIsActive(product);
            product.decreaseStock(cartItem.getQuantity());
            productRepository.save(product);

            orderItems.add(OrderItem.create(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity()
            ));
        }

        Order savedOrder = orderRepository.save(Order.checkout(memberId, orderItems));
        cartRepository.deleteByMemberId(memberId);

        return CheckoutOrderInfo.from(savedOrder);
    }

    @Transactional(noRollbackFor = PaymentFailedException.class)
    public PayOrderInfo pay(Long memberId, Long orderId, PayOrderCommand command) {
        validateMemberId(memberId);

        Order order = getOrderForUpdateWithOwnership(memberId, orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            return PayOrderInfo.success(order);
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStateException(order.getStatus(), OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        }

        boolean paid = requestPaymentWithRetry(order, command.paymentToken());
        if (!paid) {
            order.markPaymentFailed();
            restoreProductStocks(order.getItems());
            orderRepository.save(order);
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

    private void restoreProductStocks(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findByIdForUpdate(orderItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(orderItem.getProductId()));
            product.increaseStock(orderItem.getQuantity());
            productRepository.save(product);
        }
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
}

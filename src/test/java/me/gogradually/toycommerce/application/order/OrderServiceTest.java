package me.gogradually.toycommerce.application.order;

import me.gogradually.toycommerce.application.order.command.PayOrderCommand;
import me.gogradually.toycommerce.application.order.dto.CheckoutOrderInfo;
import me.gogradually.toycommerce.application.order.dto.OrderDetailInfo;
import me.gogradually.toycommerce.application.order.dto.PayOrderInfo;
import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
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
import me.gogradually.toycommerce.domain.product.exception.InsufficientProductStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCheckoutOrderAndDecreaseStock() {
        CartItem cartItem = CartItem.restore(1L, 1001L, 11L, 2, LocalDateTime.now(), LocalDateTime.now());
        Product product = activeProduct(11L, 10);

        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of(cartItem));
        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> withPersistentIdentity(invocation.getArgument(0)));

        CheckoutOrderInfo result = orderService.checkout(1001L);

        assertThat(result.orderId()).isEqualTo(500L);
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(result.totalAmount()).isEqualByComparingTo("31800");

        verify(productRepository).save(any(Product.class));
        verify(cartRepository).deleteByMemberId(1001L);
    }

    @Test
    void shouldThrowWhenCheckoutCartIsEmpty() {
        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.checkout(1001L))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void shouldThrowWhenCheckoutStockIsInsufficient() {
        CartItem cartItem = CartItem.restore(1L, 1001L, 11L, 5, LocalDateTime.now(), LocalDateTime.now());
        Product product = activeProduct(11L, 2);

        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of(cartItem));
        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.checkout(1001L))
                .isInstanceOf(InsufficientProductStockException.class);
    }

    @Test
    void shouldPayOrderSuccessfully() {
        Order pendingOrder = pendingOrder(200L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(pendingOrder));
        when(paymentGateway.pay(eq(200L), eq(1001L), any(BigDecimal.class), eq("CARD_OK")))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(true));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayOrderInfo result = orderService.pay(1001L, 200L, new PayOrderCommand("CARD_OK"));

        assertThat(result.paid()).isTrue();
        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldReturnIdempotentSuccessWhenOrderAlreadyPaid() {
        Order paidOrder = paidOrder(201L, 1001L, 11L, 2);
        when(orderRepository.findByIdForUpdate(201L)).thenReturn(Optional.of(paidOrder));

        PayOrderInfo result = orderService.pay(1001L, 201L, new PayOrderCommand("CARD_OK"));

        assertThat(result.paid()).isTrue();
        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldFailPaymentAndRestoreStock() {
        Order pendingOrder = pendingOrder(300L, 1001L, 11L, 2);
        Product product = activeProduct(11L, 8);

        when(orderRepository.findByIdForUpdate(300L)).thenReturn(Optional.of(pendingOrder));
        when(paymentGateway.pay(eq(300L), eq(1001L), any(BigDecimal.class), eq("FAIL_CARD")))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(false));
        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> orderService.pay(1001L, 300L, new PayOrderCommand("FAIL_CARD")))
                .isInstanceOf(PaymentFailedException.class);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getStock()).isEqualTo(10);
    }

    @Test
    void shouldRetryOnceWhenPaymentTimeoutOccurs() {
        Order pendingOrder = pendingOrder(400L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(400L)).thenReturn(Optional.of(pendingOrder));
        when(paymentGateway.pay(eq(400L), eq(1001L), any(BigDecimal.class), eq("TIMEOUT_CARD")))
                .thenThrow(new PaymentTimeoutException(400L, "TIMEOUT_CARD"))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(true));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayOrderInfo result = orderService.pay(1001L, 400L, new PayOrderCommand("TIMEOUT_CARD"));

        assertThat(result.paid()).isTrue();
        verify(paymentGateway, times(2)).pay(eq(400L), eq(1001L), any(BigDecimal.class), eq("TIMEOUT_CARD"));
    }

    @Test
    void shouldThrowInvalidStateWhenPayingFailedOrder() {
        Order failedOrder = failedOrder(500L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(500L)).thenReturn(Optional.of(failedOrder));

        assertThatThrownBy(() -> orderService.pay(1001L, 500L, new PayOrderCommand("CARD_OK")))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shouldGetOrderDetail() {
        Order paidOrder = paidOrder(600L, 1001L, 11L, 2);
        when(orderRepository.findById(600L)).thenReturn(Optional.of(paidOrder));

        OrderDetailInfo result = orderService.getOrder(1001L, 600L);

        assertThat(result.orderId()).isEqualTo(600L);
        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void shouldHideOrderWhenOwnerIsDifferent() {
        Order paidOrder = paidOrder(601L, 1001L, 11L, 2);
        when(orderRepository.findById(601L)).thenReturn(Optional.of(paidOrder));

        assertThatThrownBy(() -> orderService.getOrder(9999L, 601L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private Product activeProduct(Long id, int stock) {
        return Product.restore(
                id,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                stock,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2)
        );
    }

    private Order pendingOrder(Long orderId, Long memberId, Long productId, int quantity) {
        OrderItem item = OrderItem.restore(
                1L,
                orderId,
                productId,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                quantity,
                new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(2)
        );
        return Order.restore(
                orderId,
                memberId,
                OrderStatus.PENDING_PAYMENT,
                item.getLineTotal(),
                List.of(item),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(2)
        );
    }

    private Order paidOrder(Long orderId, Long memberId, Long productId, int quantity) {
        Order order = pendingOrder(orderId, memberId, productId, quantity);
        order.markPaid();
        return order;
    }

    private Order failedOrder(Long orderId, Long memberId, Long productId, int quantity) {
        Order order = pendingOrder(orderId, memberId, productId, quantity);
        order.markPaymentFailed();
        return order;
    }

    private Order withPersistentIdentity(Order order) {
        AtomicLong itemSequence = new AtomicLong(1);
        List<OrderItem> persistedItems = order.getItems().stream()
                .map(item -> OrderItem.restore(
                        itemSequence.getAndIncrement(),
                        500L,
                        item.getProductId(),
                        item.getProductNameSnapshot(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getLineTotal(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ))
                .toList();

        return Order.restore(
                500L,
                order.getMemberId(),
                order.getStatus(),
                order.getTotalAmount(),
                persistedItems,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}

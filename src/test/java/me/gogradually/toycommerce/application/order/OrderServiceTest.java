package me.gogradually.toycommerce.application.order;

import me.gogradually.toycommerce.application.order.command.CompleteOrderDetailsCommand;
import me.gogradually.toycommerce.application.order.command.PayOrderCommand;
import me.gogradually.toycommerce.application.order.dto.CheckoutOrderInfo;
import me.gogradually.toycommerce.application.order.dto.CompleteOrderDetailsInfo;
import me.gogradually.toycommerce.application.order.dto.OrderDetailInfo;
import me.gogradually.toycommerce.application.order.dto.PayOrderInfo;
import me.gogradually.toycommerce.application.order.event.OrderCreatedEvent;
import me.gogradually.toycommerce.application.order.event.OrderInfoCompletedEvent;
import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import me.gogradually.toycommerce.domain.order.*;
import me.gogradually.toycommerce.domain.order.exception.EmptyCartException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderStateException;
import me.gogradually.toycommerce.domain.order.exception.OrderNotFoundException;
import me.gogradually.toycommerce.domain.order.exception.PaymentTimeoutException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCheckoutOrderAndKeepCart() {
        CartItem cartItem = cartItem(1L, 1001L, 11L, 2);
        Product product = activeProduct(11L, 10);

        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of(cartItem));
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> withPersistentIdentity(invocation.getArgument(0), 500L));

        CheckoutOrderInfo result = orderService.checkout(1001L);

        assertThat(result.orderId()).isEqualTo(500L);
        assertThat(result.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.totalAmount()).isEqualByComparingTo("31800");
        verify(cartRepository, never()).deleteByMemberId(1001L);
        verify(applicationEventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void shouldReadProductsInAscendingProductIdOrderWhenCheckout() {
        CartItem firstCartItem = cartItem(1L, 1001L, 20L, 1);
        CartItem secondCartItem = cartItem(2L, 1001L, 11L, 2);
        Product firstProduct = activeProduct(11L, 10);
        Product secondProduct = activeProduct(20L, 10);

        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of(firstCartItem, secondCartItem));
        when(productRepository.findById(11L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(20L)).thenReturn(Optional.of(secondProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> withPersistentIdentity(invocation.getArgument(0), 500L));

        orderService.checkout(1001L);

        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository, times(2)).findById(productIdCaptor.capture());
        assertThat(productIdCaptor.getAllValues()).containsExactly(11L, 20L);
    }

    @Test
    void shouldKeepOriginalCartOrderForOrderItemsWhenCheckout() {
        CartItem firstCartItem = cartItem(1L, 1001L, 20L, 1);
        CartItem secondCartItem = cartItem(2L, 1001L, 11L, 2);
        Product firstProduct = activeProduct(11L, 10);
        Product secondProduct = activeProduct(20L, 10);

        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of(firstCartItem, secondCartItem));
        when(productRepository.findById(11L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(20L)).thenReturn(Optional.of(secondProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> withPersistentIdentity(invocation.getArgument(0), 500L));

        orderService.checkout(1001L);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getItems())
                .extracting(OrderItem::getProductId)
                .containsExactly(20L, 11L);
    }

    @Test
    void shouldThrowWhenCheckoutCartIsEmpty() {
        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.checkout(1001L))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void shouldThrowWhenCheckoutProductIsInactive() {
        CartItem cartItem = cartItem(1L, 1001L, 11L, 1);
        Product product = Product.restore(
                11L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                10,
                ProductStatus.INACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2)
        );

        when(cartRepository.findByMemberId(1001L)).thenReturn(List.of(cartItem));
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.checkout(1001L))
                .isInstanceOf(InactiveCartProductException.class);
    }

    @Test
    void shouldCompleteOrderDetails() {
        Order createdOrder = createdOrder(200L, 1001L, 11L, 2);
        when(orderRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(createdOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompleteOrderDetailsInfo result = orderService.completeOrderDetails(
                1001L,
                200L,
                new CompleteOrderDetailsCommand(
                        "홍길동",
                        "01012345678",
                        "06236",
                        "서울특별시 강남구 테헤란로 123",
                        "101동 202호",
                        "WELCOME10",
                        PaymentMethod.CARD
                )
        );

        assertThat(result.status()).isEqualTo(OrderStatus.INFO_COMPLETED);
        assertThat(result.discountAmount()).isEqualByComparingTo("3180");
        assertThat(result.totalAmount()).isEqualByComparingTo("28620");
        verify(applicationEventPublisher).publishEvent(any(OrderInfoCompletedEvent.class));
    }

    @Test
    void shouldPayOrderSuccessfullyAndDeleteMatchedCartLine() {
        Order order = infoCompletedOrder(200L, 1001L, 11L, 2);
        CartItem cartItem = cartItem(1L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(order));
        when(paymentGateway.pay(eq(200L), eq(1001L), any(BigDecimal.class), eq("CARD_OK")))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(true));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.findByMemberIdAndProductId(1001L, 11L)).thenReturn(Optional.of(cartItem));

        PayOrderInfo result = orderService.pay(1001L, 200L, new PayOrderCommand("CARD_OK"));

        assertThat(result.paid()).isTrue();
        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
        verify(cartRepository).deleteByMemberIdAndProductId(1001L, 11L);
        verify(cartRepository, never()).save(any(CartItem.class));
    }

    @Test
    void shouldPayOrderSuccessfullyAndReduceCartQuantity() {
        Order order = infoCompletedOrder(201L, 1001L, 11L, 2);
        CartItem cartItem = cartItem(1L, 1001L, 11L, 5);

        when(orderRepository.findByIdForUpdate(201L)).thenReturn(Optional.of(order));
        when(paymentGateway.pay(eq(201L), eq(1001L), any(BigDecimal.class), eq("CARD_OK")))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(true));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.findByMemberIdAndProductId(1001L, 11L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.pay(1001L, 201L, new PayOrderCommand("CARD_OK"));

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(cartItemCaptor.capture());
        assertThat(cartItemCaptor.getValue().getQuantity()).isEqualTo(3);
        verify(cartRepository, never()).deleteByMemberIdAndProductId(1001L, 11L);
    }

    @Test
    void shouldReturnIdempotentSuccessWhenOrderAlreadyPaid() {
        Order paidOrder = paidOrder(202L, 1001L, 11L, 2);
        when(orderRepository.findByIdForUpdate(202L)).thenReturn(Optional.of(paidOrder));

        PayOrderInfo result = orderService.pay(1001L, 202L, new PayOrderCommand("CARD_OK"));

        assertThat(result.paid()).isTrue();
        assertThat(result.status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldCreateReplacementOrderWhenPaymentFails() {
        Order order = infoCompletedOrder(300L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(300L)).thenReturn(Optional.of(order));
        when(paymentGateway.pay(eq(300L), eq(1001L), any(BigDecimal.class), eq("FAIL_CARD")))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(false));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order saved = invocation.getArgument(0);
                    long orderId = saved.getStatus() == OrderStatus.PAYMENT_FAILED ? 300L : 301L;
                    return withPersistentIdentity(saved, orderId);
                });

        PayOrderInfo result = orderService.pay(1001L, 300L, new PayOrderCommand("FAIL_CARD"));

        assertThat(result.paid()).isFalse();
        assertThat(result.status()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        assertThat(result.replacementOrderId()).isEqualTo(301L);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(2)).save(orderCaptor.capture());
        assertThat(orderCaptor.getAllValues())
                .extracting(Order::getStatus)
                .containsExactly(OrderStatus.PAYMENT_FAILED, OrderStatus.CREATED);
        assertThat(orderCaptor.getAllValues().get(1).getOrderDetails().getReceiverName()).isEqualTo("홍길동");
    }

    @Test
    void shouldRetryOnceWhenPaymentTimeoutOccurs() {
        Order order = infoCompletedOrder(400L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(400L)).thenReturn(Optional.of(order));
        when(paymentGateway.pay(eq(400L), eq(1001L), any(BigDecimal.class), eq("TIMEOUT_CARD")))
                .thenThrow(new PaymentTimeoutException(400L, "TIMEOUT_CARD"))
                .thenReturn(new PaymentGateway.PaymentGatewayResult(true));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.findByMemberIdAndProductId(1001L, 11L)).thenReturn(Optional.empty());

        PayOrderInfo result = orderService.pay(1001L, 400L, new PayOrderCommand("TIMEOUT_CARD"));

        assertThat(result.paid()).isTrue();
        verify(paymentGateway, times(2)).pay(eq(400L), eq(1001L), any(BigDecimal.class), eq("TIMEOUT_CARD"));
    }

    @Test
    void shouldThrowInvalidStateWhenPayingCreatedOrder() {
        Order createdOrder = createdOrder(500L, 1001L, 11L, 2);

        when(orderRepository.findByIdForUpdate(500L)).thenReturn(Optional.of(createdOrder));

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
        assertThat(result.originalAmount()).isEqualByComparingTo("31800");
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

    private CartItem cartItem(Long id, Long memberId, Long productId, int quantity) {
        return CartItem.restore(id, memberId, productId, quantity, LocalDateTime.now(), LocalDateTime.now());
    }

    private OrderItem orderItem(Long id, Long orderId, Long productId, int quantity) {
        return OrderItem.restore(
                id,
                orderId,
                productId,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                quantity,
                new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(2)
        );
    }

    private Order createdOrder(Long orderId, Long memberId, Long productId, int quantity) {
        OrderItem item = orderItem(1L, orderId, productId, quantity);
        BigDecimal originalAmount = item.getLineTotal();

        return Order.restore(
                orderId,
                memberId,
                OrderStatus.CREATED,
                OrderDetails.empty(),
                originalAmount,
                BigDecimal.ZERO,
                originalAmount,
                List.of(item),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(2)
        );
    }

    private Order infoCompletedOrder(Long orderId, Long memberId, Long productId, int quantity) {
        OrderItem item = orderItem(1L, orderId, productId, quantity);
        BigDecimal originalAmount = item.getLineTotal();

        return Order.restore(
                orderId,
                memberId,
                OrderStatus.INFO_COMPLETED,
                OrderDetails.complete(
                        "홍길동",
                        "01012345678",
                        "06236",
                        "서울특별시 강남구 테헤란로 123",
                        "101동 202호",
                        null,
                        PaymentMethod.CARD
                ),
                originalAmount,
                BigDecimal.ZERO,
                originalAmount,
                List.of(item),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(2)
        );
    }

    private Order paidOrder(Long orderId, Long memberId, Long productId, int quantity) {
        Order order = infoCompletedOrder(orderId, memberId, productId, quantity);
        order.markPaid();
        return order;
    }

    private Order withPersistentIdentity(Order order, Long orderId) {
        AtomicLong itemSequence = new AtomicLong(1);
        List<OrderItem> persistedItems = order.getItems().stream()
                .map(item -> OrderItem.restore(
                        itemSequence.getAndIncrement(),
                        orderId,
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
                orderId,
                order.getMemberId(),
                order.getStatus(),
                order.getOrderDetails(),
                order.getOriginalAmount(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                persistedItems,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}

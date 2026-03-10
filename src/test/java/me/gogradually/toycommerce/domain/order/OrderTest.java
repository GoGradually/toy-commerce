package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.EmptyCartException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderCouponException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateCreatedOrderWithCalculatedTotal() {
        OrderItem item1 = OrderItem.create(1L, "레고 스타터 세트", new BigDecimal("15900"), 2);
        OrderItem item2 = OrderItem.create(2L, "자동차 블록", new BigDecimal("5000"), 1);

        Order order = Order.checkout(1001L, List.of(item1, item2));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getOriginalAmount()).isEqualByComparingTo("36800");
        assertThat(order.getDiscountAmount()).isEqualByComparingTo("0");
        assertThat(order.getTotalAmount()).isEqualByComparingTo("36800");
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    void shouldThrowWhenCheckoutCartIsEmpty() {
        assertThatThrownBy(() -> Order.checkout(1001L, List.of()))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void shouldCompleteDetailsWithCouponDiscount() {
        Order order = createRestoredOrder(OrderStatus.CREATED);

        order.completeDetails(OrderDetails.complete(
                "홍길동",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                "101동 202호",
                "WELCOME10",
                PaymentMethod.CARD
        ));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.INFO_COMPLETED);
        assertThat(order.getDiscountAmount()).isEqualByComparingTo("3180");
        assertThat(order.getTotalAmount()).isEqualByComparingTo("28620");
        assertThat(order.getOrderDetails().getCouponCode()).isEqualTo("WELCOME10");
    }

    @Test
    void shouldThrowWhenCouponIsUnsupported() {
        Order order = createRestoredOrder(OrderStatus.CREATED);

        assertThatThrownBy(() -> order.completeDetails(OrderDetails.complete(
                "홍길동",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                "101동 202호",
                "UNKNOWN",
                PaymentMethod.CARD
        ))).isInstanceOf(InvalidOrderCouponException.class);
    }

    @Test
    void shouldMarkPaidWhenInfoCompleted() {
        Order order = infoCompletedOrder(11L, 1001L, 11L, 2);

        boolean changed = order.markPaid();

        assertThat(changed).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldBeIdempotentWhenAlreadyPaid() {
        Order order = paidOrder(11L, 1001L, 11L, 2);

        boolean changed = order.markPaid();

        assertThat(changed).isFalse();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowWhenMarkPaidFromCreatedState() {
        Order order = createRestoredOrder(OrderStatus.CREATED);

        assertThatThrownBy(order::markPaid)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shouldMarkPaymentFailedWhenInfoCompleted() {
        Order order = infoCompletedOrder(11L, 1001L, 11L, 2);

        order.markPaymentFailed();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    private Order createRestoredOrder(OrderStatus status) {
        OrderItem item = OrderItem.restore(
                1L,
                11L,
                100L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                2,
                new BigDecimal("31800"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        return Order.restore(
                11L,
                1001L,
                status,
                OrderDetails.empty(),
                new BigDecimal("31800"),
                BigDecimal.ZERO,
                new BigDecimal("31800"),
                List.of(item),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private Order infoCompletedOrder(Long orderId, Long memberId, Long productId, int quantity) {
        Order order = createRestoredOrder(OrderStatus.CREATED);
        order.completeDetails(OrderDetails.complete(
                "홍길동",
                "01012345678",
                "06236",
                "서울특별시 강남구 테헤란로 123",
                "101동 202호",
                null,
                PaymentMethod.CARD
        ));

        return Order.restore(
                orderId,
                memberId,
                OrderStatus.INFO_COMPLETED,
                order.getOrderDetails(),
                new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                BigDecimal.ZERO,
                new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                List.of(OrderItem.restore(
                        1L,
                        orderId,
                        productId,
                        "레고 스타터 세트",
                        new BigDecimal("15900"),
                        quantity,
                        new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().minusDays(1)
                )),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private Order paidOrder(Long orderId, Long memberId, Long productId, int quantity) {
        Order order = infoCompletedOrder(orderId, memberId, productId, quantity);
        order.markPaid();
        return order;
    }
}

package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.EmptyCartException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreatePendingPaymentOrderWithCalculatedTotal() {
        OrderItem item1 = OrderItem.create(1L, "레고 스타터 세트", new BigDecimal("15900"), 2);
        OrderItem item2 = OrderItem.create(2L, "자동차 블록", new BigDecimal("5000"), 1);

        Order order = Order.checkout(1001L, List.of(item1, item2));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("36800");
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    void shouldThrowWhenCheckoutCartIsEmpty() {
        assertThatThrownBy(() -> Order.checkout(1001L, List.of()))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void shouldMarkPaidWhenPendingPayment() {
        Order order = createRestoredOrder(OrderStatus.PENDING_PAYMENT);

        boolean changed = order.markPaid();

        assertThat(changed).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldBeIdempotentWhenAlreadyPaid() {
        Order order = createRestoredOrder(OrderStatus.PAID);

        boolean changed = order.markPaid();

        assertThat(changed).isFalse();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowWhenMarkPaidFromFailedState() {
        Order order = createRestoredOrder(OrderStatus.PAYMENT_FAILED);

        assertThatThrownBy(order::markPaid)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shouldMarkPaymentFailedWhenPendingPayment() {
        Order order = createRestoredOrder(OrderStatus.PENDING_PAYMENT);

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
                new BigDecimal("31800"),
                List.of(item),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}

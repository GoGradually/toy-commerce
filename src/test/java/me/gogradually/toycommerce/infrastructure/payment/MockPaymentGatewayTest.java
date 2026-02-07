package me.gogradually.toycommerce.infrastructure.payment;

import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
import me.gogradually.toycommerce.domain.order.exception.PaymentTimeoutException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MockPaymentGatewayTest {

    private final MockPaymentGateway paymentGateway = new MockPaymentGateway();

    @Test
    void shouldFailWhenPaymentTokenIsBlank() {
        PaymentGateway.PaymentGatewayResult result = paymentGateway.pay(
                1L,
                1001L,
                new BigDecimal("10000"),
                " "
        );

        assertThat(result.success()).isFalse();
    }

    @Test
    void shouldThrowTimeoutExceptionWhenTokenStartsWithTimeoutPrefix() {
        assertThatThrownBy(() -> paymentGateway.pay(
                2L,
                1002L,
                new BigDecimal("12000"),
                "TIMEOUT_CARD"
        )).isInstanceOf(PaymentTimeoutException.class);
    }

    @Test
    void shouldFailWhenTokenStartsWithFailPrefix() {
        PaymentGateway.PaymentGatewayResult result = paymentGateway.pay(
                3L,
                1003L,
                new BigDecimal("9000"),
                "FAIL_CARD"
        );

        assertThat(result.success()).isFalse();
    }

    @Test
    void shouldSucceedWhenTokenIsValid() {
        PaymentGateway.PaymentGatewayResult result = paymentGateway.pay(
                4L,
                1004L,
                new BigDecimal("25000"),
                "CARD_OK"
        );

        assertThat(result.success()).isTrue();
    }
}


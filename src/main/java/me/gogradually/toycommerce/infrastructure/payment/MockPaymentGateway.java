package me.gogradually.toycommerce.infrastructure.payment;

import me.gogradually.toycommerce.application.order.payment.PaymentGateway;
import me.gogradually.toycommerce.domain.order.exception.PaymentTimeoutException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResult pay(Long orderId, Long memberId, BigDecimal amount, String paymentToken) {
        if (paymentToken == null || paymentToken.isBlank()) {
            return new PaymentGatewayResult(false);
        }

        if (paymentToken.startsWith("TIMEOUT_")) {
            throw new PaymentTimeoutException(orderId, paymentToken);
        }

        if (paymentToken.startsWith("FAIL_")) {
            return new PaymentGatewayResult(false);
        }

        return new PaymentGatewayResult(true);
    }
}

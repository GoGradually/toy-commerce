package me.gogradually.toycommerce.application.order.payment;

import java.math.BigDecimal;

public interface PaymentGateway {

    PaymentGatewayResult pay(Long orderId, Long memberId, BigDecimal amount, String paymentToken);

    record PaymentGatewayResult(boolean success) {
    }
}

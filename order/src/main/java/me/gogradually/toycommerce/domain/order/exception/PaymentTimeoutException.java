package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class PaymentTimeoutException extends ToyCommerceException {

    public PaymentTimeoutException(Long orderId, String paymentToken) {
        super(
                "Payment gateway timeout.",
                Map.of(
                        "orderId", String.valueOf(orderId),
                        "paymentToken", String.valueOf(paymentToken)
                )
        );
    }
}

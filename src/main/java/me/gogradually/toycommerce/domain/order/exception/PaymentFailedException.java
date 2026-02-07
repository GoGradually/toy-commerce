package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class PaymentFailedException extends ToyCommerceException {

    public PaymentFailedException(Long orderId) {
        super(
                "Payment simulation failed.",
                Map.of("orderId", String.valueOf(orderId))
        );
    }
}

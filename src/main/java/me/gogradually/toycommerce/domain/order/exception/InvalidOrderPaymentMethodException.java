package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderPaymentMethodException extends ToyCommerceException {

    public InvalidOrderPaymentMethodException(Object paymentMethod) {
        super(
                "Order paymentMethod must not be null.",
                Map.of("paymentMethod", String.valueOf(paymentMethod))
        );
    }
}

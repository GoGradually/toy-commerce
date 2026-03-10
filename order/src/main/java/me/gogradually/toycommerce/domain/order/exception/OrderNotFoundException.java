package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class OrderNotFoundException extends ToyCommerceException {

    public OrderNotFoundException(Long orderId) {
        super(
                "Order was not found.",
                Map.of("orderId", String.valueOf(orderId))
        );
    }
}

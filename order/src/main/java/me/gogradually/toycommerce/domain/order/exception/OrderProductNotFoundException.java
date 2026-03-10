package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class OrderProductNotFoundException extends ToyCommerceException {

    public OrderProductNotFoundException(Long productId) {
        super(
                "Order product was not found.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidProductStatusException extends ToyCommerceException {

    public InvalidProductStatusException(Object status) {
        super(
                "Product status must not be null.",
                Map.of("status", String.valueOf(status))
        );
    }
}

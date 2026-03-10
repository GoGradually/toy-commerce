package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidProductNameException extends ToyCommerceException {

    public InvalidProductNameException(String name) {
        super(
                "Product name must not be blank.",
                Map.of("name", String.valueOf(name))
        );
    }
}

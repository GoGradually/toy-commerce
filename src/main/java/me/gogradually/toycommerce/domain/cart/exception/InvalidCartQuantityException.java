package me.gogradually.toycommerce.domain.cart.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidCartQuantityException extends ToyCommerceException {

    public InvalidCartQuantityException(int quantity) {
        super(
                "Cart quantity must be greater than 0.",
                Map.of("quantity", String.valueOf(quantity))
        );
    }
}

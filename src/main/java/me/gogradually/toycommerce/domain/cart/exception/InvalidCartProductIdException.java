package me.gogradually.toycommerce.domain.cart.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidCartProductIdException extends ToyCommerceException {

    public InvalidCartProductIdException(Long productId) {
        super(
                "Cart productId must be greater than 0.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

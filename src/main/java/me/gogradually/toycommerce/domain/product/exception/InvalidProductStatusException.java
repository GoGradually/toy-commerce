package me.gogradually.toycommerce.domain.product.exception;

import java.util.Map;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;

public class InvalidProductStatusException extends ToyCommerceException {

    public InvalidProductStatusException(Object status) {
        super(
                "Product status must not be null.",
                Map.of("status", String.valueOf(status))
        );
    }
}

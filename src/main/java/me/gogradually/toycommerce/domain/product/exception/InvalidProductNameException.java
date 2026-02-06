package me.gogradually.toycommerce.domain.product.exception;

import java.util.Map;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;

public class InvalidProductNameException extends ToyCommerceException {

    public InvalidProductNameException(String name) {
        super(
                "Product name must not be blank.",
                Map.of("name", String.valueOf(name))
        );
    }
}

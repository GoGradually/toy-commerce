package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.math.BigDecimal;
import java.util.Map;

public class InvalidProductPriceException extends ToyCommerceException {

    public InvalidProductPriceException(BigDecimal price) {
        super(
                "Product price must be zero or greater.",
                Map.of("price", String.valueOf(price))
        );
    }
}

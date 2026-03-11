package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class ProductNotFoundException extends ToyCommerceException {

    public ProductNotFoundException(Long productId) {
        super(
                "Product was not found.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

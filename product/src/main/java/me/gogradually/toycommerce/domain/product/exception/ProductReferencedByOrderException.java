package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class ProductReferencedByOrderException extends ToyCommerceException {

    public ProductReferencedByOrderException(Long productId) {
        super(
                "Product is referenced by existing orders.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

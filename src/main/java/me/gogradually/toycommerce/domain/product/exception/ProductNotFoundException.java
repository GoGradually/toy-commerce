package me.gogradually.toycommerce.domain.product.exception;

import java.util.Map;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;

public class ProductNotFoundException extends ToyCommerceException {

    public ProductNotFoundException(Long productId) {
        super(
                "Product was not found.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

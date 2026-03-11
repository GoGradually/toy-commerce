package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.util.Map;

public class InactiveCartProductException extends ToyCommerceException {

    public InactiveCartProductException(Long productId, ProductStatus status) {
        super(
                "Inactive product cannot be added to cart.",
                Map.of(
                        "productId", String.valueOf(productId),
                        "status", String.valueOf(status)
                )
        );
    }
}

package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.util.Map;

public class InactiveProductException extends ToyCommerceException {

    public InactiveProductException(Long productId, ProductStatus status) {
        super(
                "Inactive product cannot be wishlisted.",
                Map.of(
                        "productId", String.valueOf(productId),
                        "status", String.valueOf(status)
                )
        );
    }
}

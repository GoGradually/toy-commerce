package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class ActiveProductDeletionException extends ToyCommerceException {

    public ActiveProductDeletionException(Long productId) {
        super(
                "Active product cannot be deleted.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

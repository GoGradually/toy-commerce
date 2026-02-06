package me.gogradually.toycommerce.domain.wishlist.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidWishlistProductIdException extends ToyCommerceException {

    public InvalidWishlistProductIdException(Long productId) {
        super(
                "Wishlist productId must be greater than 0.",
                Map.of("productId", String.valueOf(productId))
        );
    }
}

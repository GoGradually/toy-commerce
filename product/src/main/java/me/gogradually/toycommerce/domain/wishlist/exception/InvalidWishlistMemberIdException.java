package me.gogradually.toycommerce.domain.wishlist.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidWishlistMemberIdException extends ToyCommerceException {

    public InvalidWishlistMemberIdException(Long memberId) {
        super(
                "Wishlist memberId must be greater than 0.",
                Map.of("memberId", String.valueOf(memberId))
        );
    }
}

package me.gogradually.toycommerce.domain.cart.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidCartMemberIdException extends ToyCommerceException {

    public InvalidCartMemberIdException(Long memberId) {
        super(
                "Cart memberId must be greater than 0.",
                Map.of("memberId", String.valueOf(memberId))
        );
    }
}

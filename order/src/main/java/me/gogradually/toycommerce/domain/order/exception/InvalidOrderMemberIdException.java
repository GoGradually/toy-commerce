package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderMemberIdException extends ToyCommerceException {

    public InvalidOrderMemberIdException(Long memberId) {
        super(
                "Order memberId must be greater than 0.",
                Map.of("memberId", String.valueOf(memberId))
        );
    }
}

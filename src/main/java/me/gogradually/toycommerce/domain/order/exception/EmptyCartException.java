package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class EmptyCartException extends ToyCommerceException {

    public EmptyCartException(Long memberId) {
        super(
                "Cannot checkout with empty cart.",
                Map.of("memberId", String.valueOf(memberId))
        );
    }
}

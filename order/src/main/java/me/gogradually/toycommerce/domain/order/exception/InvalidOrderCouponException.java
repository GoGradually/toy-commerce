package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderCouponException extends ToyCommerceException {

    public InvalidOrderCouponException(String couponCode) {
        super(
                "Order couponCode is invalid.",
                Map.of("couponCode", String.valueOf(couponCode))
        );
    }
}

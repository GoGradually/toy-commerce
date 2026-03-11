package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InactiveOrderProductException extends ToyCommerceException {

    public InactiveOrderProductException(Long productId, String status) {
        super(
                "Order product must be active.",
                Map.of(
                        "productId", String.valueOf(productId),
                        "status", String.valueOf(status)
                )
        );
    }
}

package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderAddressException extends ToyCommerceException {

    public InvalidOrderAddressException(String fieldName, String value) {
        super(
                "Order address field is invalid.",
                Map.of(
                        "fieldName", String.valueOf(fieldName),
                        "value", String.valueOf(value)
                )
        );
    }
}

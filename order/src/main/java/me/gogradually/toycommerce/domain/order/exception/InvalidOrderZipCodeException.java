package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderZipCodeException extends ToyCommerceException {

    public InvalidOrderZipCodeException(String zipCode) {
        super(
                "Order zipCode is invalid.",
                Map.of("zipCode", String.valueOf(zipCode))
        );
    }
}

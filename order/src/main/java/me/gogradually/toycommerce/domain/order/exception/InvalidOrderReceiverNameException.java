package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderReceiverNameException extends ToyCommerceException {

    public InvalidOrderReceiverNameException(String receiverName) {
        super(
                "Order receiverName must not be blank.",
                Map.of("receiverName", String.valueOf(receiverName))
        );
    }
}

package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidOrderReceiverPhoneException extends ToyCommerceException {

    public InvalidOrderReceiverPhoneException(String receiverPhone) {
        super(
                "Order receiverPhone is invalid.",
                Map.of("receiverPhone", String.valueOf(receiverPhone))
        );
    }
}

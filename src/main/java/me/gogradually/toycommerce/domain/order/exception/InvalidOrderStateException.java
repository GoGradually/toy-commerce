package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.order.OrderStatus;

import java.util.Map;

public class InvalidOrderStateException extends ToyCommerceException {

    public InvalidOrderStateException(
            OrderStatus currentStatus,
            OrderStatus expectedStatus,
            OrderStatus targetStatus
    ) {
        super(
                "Order state transition is invalid.",
                Map.of(
                        "currentStatus", String.valueOf(currentStatus),
                        "expectedStatus", String.valueOf(expectedStatus),
                        "targetStatus", String.valueOf(targetStatus)
                )
        );
    }
}

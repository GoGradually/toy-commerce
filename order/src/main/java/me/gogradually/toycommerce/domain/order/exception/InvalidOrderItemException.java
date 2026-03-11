package me.gogradually.toycommerce.domain.order.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.math.BigDecimal;
import java.util.Map;

public class InvalidOrderItemException extends ToyCommerceException {

    private InvalidOrderItemException(String debugMessage, Map<String, String> debugContext) {
        super(debugMessage, debugContext);
    }

    public static InvalidOrderItemException invalidOrderId(Long orderId) {
        return new InvalidOrderItemException(
                "Order item orderId must be greater than 0.",
                Map.of("orderId", String.valueOf(orderId))
        );
    }

    public static InvalidOrderItemException invalidProductId(Long productId) {
        return new InvalidOrderItemException(
                "Order item productId must be greater than 0.",
                Map.of("productId", String.valueOf(productId))
        );
    }

    public static InvalidOrderItemException invalidProductName(String productNameSnapshot) {
        return new InvalidOrderItemException(
                "Order item productNameSnapshot must not be blank.",
                Map.of("productNameSnapshot", String.valueOf(productNameSnapshot))
        );
    }

    public static InvalidOrderItemException invalidUnitPrice(BigDecimal unitPrice) {
        return new InvalidOrderItemException(
                "Order item unitPrice must be zero or positive.",
                Map.of("unitPrice", String.valueOf(unitPrice))
        );
    }

    public static InvalidOrderItemException invalidQuantity(int quantity) {
        return new InvalidOrderItemException(
                "Order item quantity must be greater than 0.",
                Map.of("quantity", String.valueOf(quantity))
        );
    }

    public static InvalidOrderItemException invalidLineTotal(BigDecimal expectedLineTotal, BigDecimal lineTotal) {
        return new InvalidOrderItemException(
                "Order item lineTotal must match unitPrice * quantity.",
                Map.of(
                        "expectedLineTotal", String.valueOf(expectedLineTotal),
                        "lineTotal", String.valueOf(lineTotal)
                )
        );
    }
}

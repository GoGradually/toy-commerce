package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InsufficientProductStockException extends ToyCommerceException {

    public InsufficientProductStockException(Long productId, int currentStock, int requestedQuantity) {
        super(
                "Product stock is insufficient.",
                Map.of(
                        "productId", String.valueOf(productId),
                        "currentStock", String.valueOf(currentStock),
                        "requestedQuantity", String.valueOf(requestedQuantity)
                )
        );
    }
}

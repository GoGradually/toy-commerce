package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class InvalidProductStockException extends ToyCommerceException {

    public InvalidProductStockException(int stock) {
        super(
                "Product stock must be zero or greater.",
                Map.of("stock", String.valueOf(stock))
        );
    }
}

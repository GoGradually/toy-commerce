package me.gogradually.toycommerce.domain.product.exception;

import java.util.Map;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;

public class InvalidProductStockException extends ToyCommerceException {

    public InvalidProductStockException(int stock) {
        super(
                "Product stock must be zero or greater.",
                Map.of("stock", String.valueOf(stock))
        );
    }
}

package me.gogradually.toycommerce.domain.product.exception;

import me.gogradually.toycommerce.common.exception.ToyCommerceException;

import java.util.Map;

public class ProductStockRemainingException extends ToyCommerceException {

    public ProductStockRemainingException(Long productId, int stock) {
        super(
                "Product stock must be zero before deletion.",
                Map.of(
                        "productId", String.valueOf(productId),
                        "stock", String.valueOf(stock)
                )
        );
    }
}

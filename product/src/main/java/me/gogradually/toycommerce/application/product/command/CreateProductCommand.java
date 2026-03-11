package me.gogradually.toycommerce.application.product.command;

import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        BigDecimal price,
        int stock,
        ProductStatus status
) {
}

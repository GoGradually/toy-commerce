package me.gogradually.toycommerce.application.product.command;

import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String name,
        BigDecimal price,
        ProductStatus status
) {
}

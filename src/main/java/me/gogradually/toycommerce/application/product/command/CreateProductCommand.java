package me.gogradually.toycommerce.application.product.command;

import java.math.BigDecimal;
import me.gogradually.toycommerce.domain.product.ProductStatus;

public record CreateProductCommand(
        String name,
        BigDecimal price,
        int stock,
        ProductStatus status
) {
}

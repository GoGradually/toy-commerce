package me.gogradually.toycommerce.application.product.command;

import java.math.BigDecimal;
import me.gogradually.toycommerce.domain.product.ProductStatus;

public record UpdateProductCommand(
        String name,
        BigDecimal price,
        ProductStatus status
) {
}

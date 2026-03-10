package me.gogradually.toycommerce.application.order.port;

import java.math.BigDecimal;

public record ProductSnapshot(
        Long productId,
        String name,
        BigDecimal price
) {
}

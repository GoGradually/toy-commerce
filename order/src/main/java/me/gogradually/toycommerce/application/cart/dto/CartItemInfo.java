package me.gogradually.toycommerce.application.cart.dto;

import java.math.BigDecimal;

public record CartItemInfo(
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal lineTotal
) {
}

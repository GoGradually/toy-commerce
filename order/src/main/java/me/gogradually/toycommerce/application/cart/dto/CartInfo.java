package me.gogradually.toycommerce.application.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartInfo(
        List<CartItemInfo> items,
        BigDecimal cartTotal
) {
}

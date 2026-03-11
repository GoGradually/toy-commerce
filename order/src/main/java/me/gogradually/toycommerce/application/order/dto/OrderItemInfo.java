package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.OrderItem;

import java.math.BigDecimal;

public record OrderItemInfo(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {

    public static OrderItemInfo from(OrderItem orderItem) {
        return new OrderItemInfo(
                orderItem.getProductId(),
                orderItem.getProductNameSnapshot(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity(),
                orderItem.getLineTotal()
        );
    }
}

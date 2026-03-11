package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutOrderInfo(
        Long orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemInfo> items
) {

    public static CheckoutOrderInfo from(Order order) {
        return new CheckoutOrderInfo(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderItemInfo::from)
                        .toList()
        );
    }
}

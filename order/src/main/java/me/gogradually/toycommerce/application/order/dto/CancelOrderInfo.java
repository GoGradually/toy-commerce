package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderStatus;

public record CancelOrderInfo(
        Long orderId,
        OrderStatus status
) {

    public static CancelOrderInfo from(Order order) {
        return new CancelOrderInfo(order.getId(), order.getStatus());
    }
}

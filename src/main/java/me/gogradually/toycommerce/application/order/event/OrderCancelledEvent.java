package me.gogradually.toycommerce.application.order.event;

import me.gogradually.toycommerce.domain.order.ExpiredOrderCancellationTarget;
import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderItem;

import java.util.List;

public record OrderCancelledEvent(
        Long orderId,
        List<OrderItem> items
) {

    public static OrderCancelledEvent from(Order order) {
        return new OrderCancelledEvent(order.getId(), order.getItems());
    }

    public static OrderCancelledEvent from(ExpiredOrderCancellationTarget target) {
        return new OrderCancelledEvent(target.orderId(), target.items());
    }
}

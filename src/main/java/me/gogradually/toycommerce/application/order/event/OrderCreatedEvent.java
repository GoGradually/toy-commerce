package me.gogradually.toycommerce.application.order.event;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderItem;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderItem> items
) {

    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(order.getId(), order.getItems());
    }
}

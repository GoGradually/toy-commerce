package me.gogradually.toycommerce.application.order.event;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderItem;

import java.util.List;

public record OrderPaymentFailedEvent(
        Long orderId,
        List<OrderItem> items
) {

    public static OrderPaymentFailedEvent from(Order order) {
        return new OrderPaymentFailedEvent(order.getId(), order.getItems());
    }
}

package me.gogradually.toycommerce.application.order.event;

import java.util.List;
import java.util.Objects;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderLineSnapshot> items
) {

    public OrderCreatedEvent {
        Objects.requireNonNull(orderId, "orderId must not be null");
        items = items == null ? List.of() : List.copyOf(items);
    }
}

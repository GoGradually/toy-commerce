package me.gogradually.toycommerce.domain.order;

import java.util.List;
import java.util.Objects;

public record ExpiredOrderCancellationTarget(
        Long orderId,
        List<OrderItem> items
) {

    public ExpiredOrderCancellationTarget {
        Objects.requireNonNull(orderId, "orderId must not be null");
        items = items == null ? List.of() : List.copyOf(items);
    }
}

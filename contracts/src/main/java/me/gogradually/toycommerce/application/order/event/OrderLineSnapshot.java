package me.gogradually.toycommerce.application.order.event;

public record OrderLineSnapshot(
        Long productId,
        int quantity
) {
}

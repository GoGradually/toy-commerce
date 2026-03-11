package me.gogradually.toycommerce.application.cart.command;

public record AddCartItemCommand(
        Long productId,
        int quantity
) {
}

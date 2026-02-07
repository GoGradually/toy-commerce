package me.gogradually.toycommerce.application.order.command;

public record PayOrderCommand(
        String paymentToken
) {
}

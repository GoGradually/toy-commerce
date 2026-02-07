package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderStatus;

public record PayOrderInfo(
        Long orderId,
        OrderStatus status,
        boolean paid,
        PaymentResult paymentResult
) {

    public static PayOrderInfo success(Order order) {
        return new PayOrderInfo(
                order.getId(),
                order.getStatus(),
                order.getStatus() == OrderStatus.PAID,
                PaymentResult.SUCCESS
        );
    }
}

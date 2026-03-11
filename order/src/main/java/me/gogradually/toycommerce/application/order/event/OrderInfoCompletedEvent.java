package me.gogradually.toycommerce.application.order.event;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

import java.math.BigDecimal;

public record OrderInfoCompletedEvent(
        Long orderId,
        Long memberId,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod
) {

    public static OrderInfoCompletedEvent from(Order order) {
        return new OrderInfoCompletedEvent(
                order.getId(),
                order.getMemberId(),
                order.getTotalAmount(),
                order.getOrderDetails().getPaymentMethod()
        );
    }
}

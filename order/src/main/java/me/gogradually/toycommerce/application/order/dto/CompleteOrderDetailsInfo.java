package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

import java.math.BigDecimal;

public record CompleteOrderDetailsInfo(
        Long orderId,
        OrderStatus status,
        BigDecimal originalAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String couponCode,
        PaymentMethod paymentMethod
) {

    public static CompleteOrderDetailsInfo from(Order order) {
        return new CompleteOrderDetailsInfo(
                order.getId(),
                order.getStatus(),
                order.getOriginalAmount(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getOrderDetails().getCouponCode(),
                order.getOrderDetails().getPaymentMethod()
        );
    }
}

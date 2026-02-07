package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailInfo(
        Long orderId,
        Long memberId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemInfo> items,
        LocalDateTime createdAt
) {

    public static OrderDetailInfo from(Order order) {
        return new OrderDetailInfo(
                order.getId(),
                order.getMemberId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderItemInfo::from)
                        .toList(),
                order.getCreatedAt()
        );
    }
}

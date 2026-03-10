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
        BigDecimal originalAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        List<OrderItemInfo> items,
        OrderDetailsSnapshotInfo orderDetails,
        LocalDateTime createdAt
) {

    public static OrderDetailInfo from(Order order) {
        return new OrderDetailInfo(
                order.getId(),
                order.getMemberId(),
                order.getStatus(),
                order.getOriginalAmount(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderItemInfo::from)
                        .toList(),
                OrderDetailsSnapshotInfo.from(order.getOrderDetails()),
                order.getCreatedAt()
        );
    }
}

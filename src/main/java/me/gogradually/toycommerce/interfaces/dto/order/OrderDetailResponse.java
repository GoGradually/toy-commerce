package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.OrderDetailInfo;
import me.gogradually.toycommerce.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문 상세 조회 응답")
public record OrderDetailResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,
        @Schema(description = "회원 ID", example = "1001")
        Long memberId,
        @Schema(description = "주문 상태", example = "INFO_COMPLETED")
        OrderStatus status,
        @Schema(description = "원 주문 금액", example = "31800")
        BigDecimal originalAmount,
        @Schema(description = "할인 금액", example = "3180")
        BigDecimal discountAmount,
        @Schema(description = "총 주문 금액", example = "47700")
        BigDecimal totalAmount,
        @Schema(description = "주문 항목 목록")
        List<OrderItemResponse> items,
        @Schema(description = "주문 정보 스냅샷")
        OrderDetailsSnapshotResponse orderDetails,
        @Schema(description = "주문 생성 시각", example = "2026-02-07T10:15:30")
        LocalDateTime createdAt
) {

    public static OrderDetailResponse from(OrderDetailInfo info) {
        return new OrderDetailResponse(
                info.orderId(),
                info.memberId(),
                info.status(),
                info.originalAmount(),
                info.discountAmount(),
                info.totalAmount(),
                info.items().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                OrderDetailsSnapshotResponse.from(info.orderDetails()),
                info.createdAt()
        );
    }
}

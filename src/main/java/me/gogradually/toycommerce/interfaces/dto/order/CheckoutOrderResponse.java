package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.CheckoutOrderInfo;
import me.gogradually.toycommerce.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "주문 생성 응답")
public record CheckoutOrderResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,
        @Schema(description = "주문 상태", example = "PENDING_PAYMENT")
        OrderStatus status,
        @Schema(description = "총 주문 금액", example = "47700")
        BigDecimal totalAmount,
        @Schema(description = "주문 항목 목록")
        List<OrderItemResponse> items
) {

    public static CheckoutOrderResponse from(CheckoutOrderInfo info) {
        return new CheckoutOrderResponse(
                info.orderId(),
                info.status(),
                info.totalAmount(),
                info.items().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}

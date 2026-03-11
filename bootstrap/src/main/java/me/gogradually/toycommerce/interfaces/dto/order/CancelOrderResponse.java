package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.CancelOrderInfo;
import me.gogradually.toycommerce.domain.order.OrderStatus;

@Schema(description = "주문 취소 응답")
public record CancelOrderResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,
        @Schema(description = "주문 상태", example = "CANCELLED")
        OrderStatus status
) {

    public static CancelOrderResponse from(CancelOrderInfo info) {
        return new CancelOrderResponse(info.orderId(), info.status());
    }
}

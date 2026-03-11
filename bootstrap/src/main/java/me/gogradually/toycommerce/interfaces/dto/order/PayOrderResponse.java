package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.PayOrderInfo;
import me.gogradually.toycommerce.application.order.dto.PaymentResult;
import me.gogradually.toycommerce.domain.order.OrderStatus;

@Schema(description = "주문 결제 응답")
public record PayOrderResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,
        @Schema(description = "주문 상태", example = "PAID")
        OrderStatus status,
        @Schema(description = "결제 완료 여부", example = "true")
        boolean paid,
        @Schema(description = "결제 결과", example = "SUCCESS")
        PaymentResult paymentResult,
        @Schema(description = "결제 실패 시 새로 생성된 대체 주문 ID", example = "2", nullable = true)
        Long replacementOrderId
) {

    public static PayOrderResponse from(PayOrderInfo info) {
        return new PayOrderResponse(
                info.orderId(),
                info.status(),
                info.paid(),
                info.paymentResult(),
                info.replacementOrderId()
        );
    }
}

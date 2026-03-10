package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.CompleteOrderDetailsInfo;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

import java.math.BigDecimal;

@Schema(description = "주문 정보 입력 완료 응답")
public record CompleteOrderDetailsResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,
        @Schema(description = "주문 상태", example = "INFO_COMPLETED")
        OrderStatus status,
        @Schema(description = "원 주문 금액", example = "31800")
        BigDecimal originalAmount,
        @Schema(description = "할인 금액", example = "3180")
        BigDecimal discountAmount,
        @Schema(description = "최종 주문 금액", example = "28620")
        BigDecimal totalAmount,
        @Schema(description = "적용 쿠폰 코드", example = "WELCOME10")
        String couponCode,
        @Schema(description = "결제 수단", example = "CARD")
        PaymentMethod paymentMethod
) {

    public static CompleteOrderDetailsResponse from(CompleteOrderDetailsInfo info) {
        return new CompleteOrderDetailsResponse(
                info.orderId(),
                info.status(),
                info.originalAmount(),
                info.discountAmount(),
                info.totalAmount(),
                info.couponCode(),
                info.paymentMethod()
        );
    }
}

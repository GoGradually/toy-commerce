package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.OrderDetailsSnapshotInfo;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

@Schema(description = "주문 정보 스냅샷")
public record OrderDetailsSnapshotResponse(
        @Schema(description = "수령인명", example = "홍길동")
        String receiverName,
        @Schema(description = "수령인 연락처", example = "01012345678")
        String receiverPhone,
        @Schema(description = "우편번호", example = "06236")
        String zipCode,
        @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 123")
        String addressLine1,
        @Schema(description = "상세 주소", example = "101동 202호")
        String addressLine2,
        @Schema(description = "적용 쿠폰 코드", example = "WELCOME10")
        String couponCode,
        @Schema(description = "결제 수단", example = "CARD")
        PaymentMethod paymentMethod
) {

    public static OrderDetailsSnapshotResponse from(OrderDetailsSnapshotInfo info) {
        return new OrderDetailsSnapshotResponse(
                info.receiverName(),
                info.receiverPhone(),
                info.zipCode(),
                info.addressLine1(),
                info.addressLine2(),
                info.couponCode(),
                info.paymentMethod()
        );
    }
}

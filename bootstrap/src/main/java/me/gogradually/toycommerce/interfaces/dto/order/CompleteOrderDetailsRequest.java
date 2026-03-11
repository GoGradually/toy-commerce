package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import me.gogradually.toycommerce.application.order.command.CompleteOrderDetailsCommand;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

@Schema(description = "주문 정보 입력 완료 요청")
public record CompleteOrderDetailsRequest(
        @Schema(description = "수령인명", example = "홍길동")
        @NotBlank(message = "receiverName은 필수입니다.")
        String receiverName,
        @Schema(description = "수령인 연락처", example = "01012345678")
        @NotBlank(message = "receiverPhone은 필수입니다.")
        @Pattern(regexp = "^\\d{9,11}$", message = "receiverPhone은 숫자 9~11자리여야 합니다.")
        String receiverPhone,
        @Schema(description = "우편번호", example = "06236")
        @NotBlank(message = "zipCode는 필수입니다.")
        @Pattern(regexp = "^\\d{5}$", message = "zipCode는 숫자 5자리여야 합니다.")
        String zipCode,
        @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 123")
        @NotBlank(message = "addressLine1은 필수입니다.")
        String addressLine1,
        @Schema(description = "상세 주소", example = "101동 202호")
        String addressLine2,
        @Schema(description = "쿠폰 코드", example = "WELCOME10")
        String couponCode,
        @Schema(description = "결제 수단", example = "CARD")
        @NotNull(message = "paymentMethod는 필수입니다.")
        PaymentMethod paymentMethod
) {

    public CompleteOrderDetailsCommand toCommand() {
        return new CompleteOrderDetailsCommand(
                receiverName,
                receiverPhone,
                zipCode,
                addressLine1,
                addressLine2,
                couponCode,
                paymentMethod
        );
    }
}

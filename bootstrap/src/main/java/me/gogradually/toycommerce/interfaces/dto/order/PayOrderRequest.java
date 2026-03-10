package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import me.gogradually.toycommerce.application.order.command.PayOrderCommand;

@Schema(description = "주문 결제 요청")
public record PayOrderRequest(
        @Schema(description = "결제 토큰", example = "CARD_20260207_0001")
        @NotBlank(message = "paymentToken은 필수입니다.")
        String paymentToken
) {

    public PayOrderCommand toCommand() {
        return new PayOrderCommand(paymentToken);
    }
}

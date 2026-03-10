package me.gogradually.toycommerce.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.gogradually.toycommerce.application.cart.command.UpdateCartItemQuantityCommand;

@Schema(description = "장바구니 수량 변경 요청")
public record UpdateCartItemQuantityRequest(
        @Schema(description = "수량", example = "3")
        @NotNull(message = "quantity는 필수입니다.")
        @Min(value = 1, message = "quantity는 1 이상이어야 합니다.")
        Integer quantity
) {

    public UpdateCartItemQuantityCommand toCommand() {
        return new UpdateCartItemQuantityCommand(quantity);
    }
}

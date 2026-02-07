package me.gogradually.toycommerce.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.gogradually.toycommerce.application.cart.command.AddCartItemCommand;

@Schema(description = "장바구니 상품 추가 요청")
public record AddCartItemRequest(
        @Schema(description = "상품 ID", example = "1")
        @NotNull(message = "productId는 필수입니다.")
        @Min(value = 1, message = "productId는 1 이상이어야 합니다.")
        Long productId,

        @Schema(description = "수량", example = "2")
        @NotNull(message = "quantity는 필수입니다.")
        @Min(value = 1, message = "quantity는 1 이상이어야 합니다.")
        Integer quantity
) {

    public AddCartItemCommand toCommand() {
        return new AddCartItemCommand(productId, quantity);
    }
}

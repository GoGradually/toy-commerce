package me.gogradually.toycommerce.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import me.gogradually.toycommerce.application.product.command.UpdateProductStockCommand;

@Schema(description = "상품 재고 수정 요청")
public record UpdateProductStockRequest(
        @Schema(description = "재고 수량", example = "12")
        @NotNull(message = "재고는 필수입니다.")
        @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
        Integer stock
) {

    public UpdateProductStockCommand toCommand() {
        return new UpdateProductStockCommand(stock);
    }
}

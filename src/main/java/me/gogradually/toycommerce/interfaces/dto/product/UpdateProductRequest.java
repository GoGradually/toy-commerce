package me.gogradually.toycommerce.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import me.gogradually.toycommerce.application.product.command.UpdateProductCommand;
import me.gogradually.toycommerce.domain.product.ProductStatus;

@Schema(description = "상품 수정 요청")
public record UpdateProductRequest(
        @Schema(description = "상품명", example = "레고 프로 세트")
        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        @Schema(description = "가격", example = "25900")
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @Schema(description = "상품 상태", example = "INACTIVE")
        @NotNull(message = "상품 상태는 필수입니다.")
        ProductStatus status
) {

    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(name, price, status);
    }
}

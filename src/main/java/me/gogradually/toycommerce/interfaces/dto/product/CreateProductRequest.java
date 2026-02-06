package me.gogradually.toycommerce.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import me.gogradually.toycommerce.application.product.command.CreateProductCommand;
import me.gogradually.toycommerce.domain.product.ProductStatus;

@Schema(description = "상품 생성 요청")
public record CreateProductRequest(
        @Schema(description = "상품명", example = "레고 스타터 세트")
        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        @Schema(description = "가격", example = "15900")
        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @Schema(description = "재고 수량", example = "50")
        @NotNull(message = "재고는 필수입니다.")
        @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
        Integer stock,

        @Schema(description = "상품 상태", example = "ACTIVE")
        @NotNull(message = "상품 상태는 필수입니다.")
        ProductStatus status
) {

    public CreateProductCommand toCommand() {
        return new CreateProductCommand(name, price, stock, status);
    }
}

package me.gogradually.toycommerce.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.domain.product.ProductStatus;

@Schema(description = "상품 응답")
public record ProductResponse(
        @Schema(description = "상품 ID", example = "1")
        Long id,
        @Schema(description = "상품명", example = "레고 스타터 세트")
        String name,
        @Schema(description = "가격", example = "15900")
        BigDecimal price,
        @Schema(description = "재고 수량", example = "50")
        int stock,
        @Schema(description = "상품 상태", example = "ACTIVE")
        ProductStatus status
) {

    public static ProductResponse from(ProductDetailInfo product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.price(),
                product.stock(),
                product.status()
        );
    }
}

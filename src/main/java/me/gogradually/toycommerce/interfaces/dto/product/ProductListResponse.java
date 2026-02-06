package me.gogradually.toycommerce.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import me.gogradually.toycommerce.application.product.dto.ProductPageInfo;

@Schema(description = "상품 목록 응답")
public record ProductListResponse(
        @Schema(description = "상품 목록")
        List<ProductResponse> products,
        @Schema(description = "현재 페이지", example = "0")
        int page,
        @Schema(description = "페이지 크기", example = "20")
        int size,
        @Schema(description = "전체 데이터 수", example = "100")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

    public static ProductListResponse from(ProductPageInfo productPageInfo) {
        return new ProductListResponse(
                productPageInfo.products().stream()
                        .map(ProductResponse::from)
                        .toList(),
                productPageInfo.page(),
                productPageInfo.size(),
                productPageInfo.totalElements(),
                productPageInfo.totalPages(),
                productPageInfo.hasNext()
        );
    }
}

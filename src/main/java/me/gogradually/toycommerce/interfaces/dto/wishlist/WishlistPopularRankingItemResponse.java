package me.gogradually.toycommerce.interfaces.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingItemInfo;
import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.math.BigDecimal;

@Schema(description = "인기 찜 랭킹 항목")
public record WishlistPopularRankingItemResponse(
        @Schema(description = "랭킹 순위", example = "1")
        int rank,
        @Schema(description = "상품 ID", example = "3")
        Long productId,
        @Schema(description = "상품명", example = "레고 클래식 세트")
        String name,
        @Schema(description = "가격", example = "25900")
        BigDecimal price,
        @Schema(description = "상품 상태", example = "ACTIVE")
        ProductStatus status,
        @Schema(description = "찜 수", example = "120")
        long wishlistCount
) {

    public static WishlistPopularRankingItemResponse from(WishlistPopularRankingItemInfo item) {
        return new WishlistPopularRankingItemResponse(
                item.rank(),
                item.productId(),
                item.name(),
                item.price(),
                item.status(),
                item.wishlistCount()
        );
    }
}

package me.gogradually.toycommerce.interfaces.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingInfo;

import java.util.List;

@Schema(description = "인기 찜 랭킹 응답")
public record WishlistPopularRankingResponse(
        @Schema(description = "요청 limit", example = "10")
        int limit,
        @Schema(description = "랭킹 목록")
        List<WishlistPopularRankingItemResponse> rankings
) {

    public static WishlistPopularRankingResponse from(WishlistPopularRankingInfo info, int limit) {
        return new WishlistPopularRankingResponse(
                limit,
                info.rankings().stream()
                        .map(WishlistPopularRankingItemResponse::from)
                        .toList()
        );
    }
}

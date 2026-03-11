package me.gogradually.toycommerce.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.wishlist.WishlistService;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingInfo;
import me.gogradually.toycommerce.interfaces.dto.wishlist.WishlistPopularRankingResponse;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/rankings/wishlist")
@Tag(name = "Wishlist Rankings", description = "인기 찜 랭킹 조회 API")
public class WishlistRankingController {

    private final WishlistService wishlistService;

    @GetMapping("/popular")
    @Operation(summary = "인기 찜 랭킹 조회", description = "Redis Sorted Set 기준으로 인기 찜 랭킹을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<WishlistPopularRankingResponse> getPopularWishlistRankings(
            @Parameter(description = "반환할 랭킹 개수(1~100)", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
            @Max(value = 100, message = "limit은 100 이하여야 합니다.")
            int limit
    ) {
        WishlistPopularRankingInfo rankings = wishlistService.getPopularRankings(limit);
        return ApiResponse.success(WishlistPopularRankingResponse.from(rankings, limit));
    }
}

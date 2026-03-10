package me.gogradually.toycommerce.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.wishlist.WishlistService;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Wishlist", description = "상품 찜 관리 API")
public class WishlistController {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    private final WishlistService wishlistService;

    @PostMapping("/{productId}/wishlist")
    @Operation(summary = "상품 찜 추가", description = "회원이 활성 상품을 찜 목록에 추가합니다. 이미 찜한 경우에도 성공 응답합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<Void> addWishlist(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId
    ) {
        wishlistService.addWishlist(memberId, productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{productId}/wishlist")
    @Operation(summary = "상품 찜 해제", description = "회원의 상품 찜을 해제합니다. 이미 찜이 없어도 성공 응답합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ApiResponse<Void> removeWishlist(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId
    ) {
        wishlistService.removeWishlist(memberId, productId);
        return ApiResponse.success(null);
    }
}

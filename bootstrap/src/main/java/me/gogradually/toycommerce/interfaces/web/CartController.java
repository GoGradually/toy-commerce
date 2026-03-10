package me.gogradually.toycommerce.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.cart.CartService;
import me.gogradually.toycommerce.application.cart.dto.CartInfo;
import me.gogradually.toycommerce.interfaces.dto.cart.AddCartItemRequest;
import me.gogradually.toycommerce.interfaces.dto.cart.CartResponse;
import me.gogradually.toycommerce.interfaces.dto.cart.UpdateCartItemQuantityRequest;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/cart/items")
@Tag(name = "Cart", description = "장바구니 API")
public class CartController {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "장바구니 조회", description = "회원의 장바구니 항목과 총액을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<CartResponse> getCartItems(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId
    ) {
        CartInfo info = cartService.getCartItems(memberId);
        return ApiResponse.success(CartResponse.from(info));
    }

    @PostMapping
    @Operation(summary = "장바구니 담기", description = "상품을 장바구니에 추가합니다. 이미 담긴 상품이면 수량을 합산합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<Void> addCartItem(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        cartService.addCartItem(memberId, request.toCommand());
        return ApiResponse.success(null);
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "장바구니 수량 변경", description = "장바구니 상품의 수량을 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<Void> updateCartItemQuantity(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId,
            @RequestBody @Valid UpdateCartItemQuantityRequest request
    ) {
        cartService.updateQuantity(memberId, productId, request.toCommand());
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "장바구니 단건 삭제", description = "장바구니에서 특정 상품을 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ApiResponse<Void> removeCartItem(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId
    ) {
        cartService.removeCartItem(memberId, productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping
    @Operation(summary = "장바구니 전체 비우기", description = "회원의 장바구니를 전체 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ApiResponse<Void> clearCart(
            @Parameter(description = "회원 ID 헤더", example = "1")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId
    ) {
        cartService.clearCart(memberId);
        return ApiResponse.success(null);
    }
}

package me.gogradually.toycommerce.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.cart.dto.CartInfo;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "장바구니 조회 응답")
public record CartResponse(
        @Schema(description = "장바구니 항목 목록")
        List<CartItemResponse> items,
        @Schema(description = "장바구니 총액", example = "47700")
        BigDecimal cartTotal
) {

    public static CartResponse from(CartInfo info) {
        return new CartResponse(
                info.items().stream()
                        .map(CartItemResponse::from)
                        .toList(),
                info.cartTotal()
        );
    }
}

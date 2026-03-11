package me.gogradually.toycommerce.interfaces.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.cart.dto.CartItemInfo;

import java.math.BigDecimal;

@Schema(description = "장바구니 항목")
public record CartItemResponse(
        @Schema(description = "상품 ID", example = "1")
        Long productId,
        @Schema(description = "상품명", example = "레고 스타터 세트")
        String name,
        @Schema(description = "상품 가격", example = "15900")
        BigDecimal price,
        @Schema(description = "수량", example = "2")
        int quantity,
        @Schema(description = "항목 총액", example = "31800")
        BigDecimal lineTotal
) {

    public static CartItemResponse from(CartItemInfo info) {
        return new CartItemResponse(
                info.productId(),
                info.name(),
                info.price(),
                info.quantity(),
                info.lineTotal()
        );
    }
}

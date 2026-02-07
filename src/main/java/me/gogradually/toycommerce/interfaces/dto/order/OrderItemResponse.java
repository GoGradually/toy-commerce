package me.gogradually.toycommerce.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import me.gogradually.toycommerce.application.order.dto.OrderItemInfo;

import java.math.BigDecimal;

@Schema(description = "주문 항목 응답")
public record OrderItemResponse(
        @Schema(description = "상품 ID", example = "1")
        Long productId,
        @Schema(description = "주문 시점 상품명", example = "레고 스타터 세트")
        String productName,
        @Schema(description = "주문 시점 단가", example = "15900")
        BigDecimal unitPrice,
        @Schema(description = "주문 수량", example = "2")
        int quantity,
        @Schema(description = "라인 금액", example = "31800")
        BigDecimal lineTotal
) {

    public static OrderItemResponse from(OrderItemInfo item) {
        return new OrderItemResponse(
                item.productId(),
                item.productName(),
                item.unitPrice(),
                item.quantity(),
                item.lineTotal()
        );
    }
}

package me.gogradually.toycommerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "서버 내부 오류가 발생했습니다."),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT-404", "상품을 찾을 수 없습니다."),
    INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "PRODUCT-400-NAME", "상품명은 비어 있을 수 없습니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "PRODUCT-400-PRICE", "상품 가격은 0 이상이어야 합니다."),
    INVALID_PRODUCT_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT-400-STOCK", "상품 재고는 0 이상이어야 합니다."),
    INACTIVE_PRODUCT(HttpStatus.BAD_REQUEST, "PRODUCT-400-INACTIVE", "비활성 상품은 찜할 수 없습니다."),

    INVALID_CART_MEMBER(HttpStatus.BAD_REQUEST, "CART-400-MEMBER", "memberId는 1 이상이어야 합니다."),
    INVALID_CART_PRODUCT(HttpStatus.BAD_REQUEST, "CART-400-PRODUCT", "productId는 1 이상이어야 합니다."),
    INVALID_CART_QUANTITY(HttpStatus.BAD_REQUEST, "CART-400-QUANTITY", "수량은 1 이상이어야 합니다."),
    INACTIVE_CART_PRODUCT(HttpStatus.BAD_REQUEST, "CART-400-INACTIVE", "비활성 상품은 장바구니에 담을 수 없습니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER-404", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_MEMBER(HttpStatus.BAD_REQUEST, "ORDER-400-MEMBER", "memberId는 1 이상이어야 합니다."),
    EMPTY_ORDER_CART(HttpStatus.BAD_REQUEST, "ORDER-400-EMPTY-CART", "장바구니가 비어 있습니다."),
    INVALID_ORDER_STATE(HttpStatus.BAD_REQUEST, "ORDER-400-STATE", "현재 주문 상태에서 요청을 처리할 수 없습니다."),
    INSUFFICIENT_ORDER_STOCK(HttpStatus.BAD_REQUEST, "ORDER-400-STOCK", "재고가 부족합니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT-400-FAILED", "결제에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

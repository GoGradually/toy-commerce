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
    INVALID_PRODUCT_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT-400-STOCK", "상품 재고는 0 이상이어야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

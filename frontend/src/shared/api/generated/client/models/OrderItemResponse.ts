/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 주문 항목 응답
 */
export type OrderItemResponse = {
    /**
     * 상품 ID
     */
    productId?: number;
    /**
     * 주문 시점 상품명
     */
    productName?: string;
    /**
     * 주문 시점 단가
     */
    unitPrice?: number;
    /**
     * 주문 수량
     */
    quantity?: number;
    /**
     * 라인 금액
     */
    lineTotal?: number;
};


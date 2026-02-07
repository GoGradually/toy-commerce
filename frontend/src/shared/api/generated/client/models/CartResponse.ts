/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {CartItemResponse} from './CartItemResponse';

/**
 * 장바구니 조회 응답
 */
export type CartResponse = {
    /**
     * 장바구니 항목 목록
     */
    items?: Array<CartItemResponse>;
    /**
     * 장바구니 총액
     */
    cartTotal?: number;
};


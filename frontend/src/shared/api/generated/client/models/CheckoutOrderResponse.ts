/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {OrderItemResponse} from './OrderItemResponse';

/**
 * 주문 생성 응답
 */
export type CheckoutOrderResponse = {
    /**
     * 주문 ID
     */
    orderId?: number;
    /**
     * 주문 상태
     */
    status?: 'PENDING_PAYMENT' | 'PAID' | 'PAYMENT_FAILED';
    /**
     * 총 주문 금액
     */
    totalAmount?: number;
    /**
     * 주문 항목 목록
     */
    items?: Array<OrderItemResponse>;
};


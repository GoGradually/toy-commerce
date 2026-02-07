/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 주문 결제 응답
 */
export type PayOrderResponse = {
    /**
     * 주문 ID
     */
    orderId?: number;
    /**
     * 주문 상태
     */
    status?: 'PENDING_PAYMENT' | 'PAID' | 'PAYMENT_FAILED';
    /**
     * 결제 완료 여부
     */
    paid?: boolean;
    /**
     * 결제 결과
     */
    paymentResult?: 'SUCCESS' | 'FAILED';
};


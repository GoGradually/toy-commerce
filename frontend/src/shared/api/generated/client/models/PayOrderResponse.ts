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
    status?: 'CREATED' | 'INFO_COMPLETED' | 'PAID' | 'PAYMENT_FAILED' | 'CANCELLED';
    /**
     * 결제 완료 여부
     */
    paid?: boolean;
    /**
     * 결제 결과
     */
    paymentResult?: 'SUCCESS' | 'FAILED';
    /**
     * 결제 실패 시 새로 생성된 대체 주문 ID
     */
    replacementOrderId?: number | null;
};

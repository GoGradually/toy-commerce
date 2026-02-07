/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {CheckoutOrderResponse} from './CheckoutOrderResponse';
import type {ErrorBody} from './ErrorBody';

/**
 * 공통 API 응답
 */
export type ApiResponseCheckoutOrderResponse = {
    /**
     * 요청 성공 여부
     */
    success?: boolean;
    /**
     * 응답 데이터
     */
    data?: CheckoutOrderResponse;
    /**
     * 에러 정보
     */
    error?: ErrorBody;
};


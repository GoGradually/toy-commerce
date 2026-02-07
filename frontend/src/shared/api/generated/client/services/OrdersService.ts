/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ApiResponseCheckoutOrderResponse} from '../models/ApiResponseCheckoutOrderResponse';
import type {ApiResponseOrderDetailResponse} from '../models/ApiResponseOrderDetailResponse';
import type {ApiResponsePayOrderResponse} from '../models/ApiResponsePayOrderResponse';
import type {PayOrderRequest} from '../models/PayOrderRequest';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';

export class OrdersService {
    /**
     * 주문 결제
     * 결제를 모사하여 주문 상태를 변경합니다.
     * @returns ApiResponsePayOrderResponse 결제 성공/멱등 성공
     * @throws ApiError
     */
    public static pay({
                          xMemberId,
                          orderId,
                          requestBody,
                      }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
        /**
         * 주문 ID
         */
        orderId: number,
        requestBody: PayOrderRequest,
    }): CancelablePromise<ApiResponsePayOrderResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/orders/{orderId}/pay',
            path: {
                'orderId': orderId,
            },
            headers: {
                'X-Member-Id': xMemberId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `결제 실패 또는 잘못된 상태`,
                404: `주문 미존재`,
            },
        });
    }

    /**
     * 주문 생성
     * 회원의 장바구니를 주문으로 생성하고 재고를 차감합니다.
     * @returns ApiResponseCheckoutOrderResponse 주문 생성 성공
     * @throws ApiError
     */
    public static checkout({
                               xMemberId,
                           }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
    }): CancelablePromise<ApiResponseCheckoutOrderResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/orders/checkout',
            headers: {
                'X-Member-Id': xMemberId,
            },
            errors: {
                400: `잘못된 요청 또는 주문 생성 실패`,
            },
        });
    }

    /**
     * 주문 상세 조회
     * 주문 상세와 항목 정보를 조회합니다.
     * @returns ApiResponseOrderDetailResponse 조회 성공
     * @throws ApiError
     */
    public static getOrder({
                               xMemberId,
                               orderId,
                           }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
        /**
         * 주문 ID
         */
        orderId: number,
    }): CancelablePromise<ApiResponseOrderDetailResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/orders/{orderId}',
            path: {
                'orderId': orderId,
            },
            headers: {
                'X-Member-Id': xMemberId,
            },
            errors: {
                404: `주문 미존재`,
            },
        });
    }
}

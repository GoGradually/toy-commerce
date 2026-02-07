/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {AddCartItemRequest} from '../models/AddCartItemRequest';
import type {ApiResponseCartResponse} from '../models/ApiResponseCartResponse';
import type {ApiResponseVoid} from '../models/ApiResponseVoid';
import type {UpdateCartItemQuantityRequest} from '../models/UpdateCartItemQuantityRequest';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';

export class CartService {
    /**
     * 장바구니 조회
     * 회원의 장바구니 항목과 총액을 조회합니다.
     * @returns ApiResponseCartResponse 조회 성공
     * @throws ApiError
     */
    public static getCartItems({
                                   xMemberId,
                               }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
    }): CancelablePromise<ApiResponseCartResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/cart/items',
            headers: {
                'X-Member-Id': xMemberId,
            },
            errors: {
                400: `잘못된 요청 값`,
            },
        });
    }

    /**
     * 장바구니 담기
     * 상품을 장바구니에 추가합니다. 이미 담긴 상품이면 수량을 합산합니다.
     * @returns ApiResponseVoid 요청 성공
     * @throws ApiError
     */
    public static addCartItem({
                                  xMemberId,
                                  requestBody,
                              }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
        requestBody: AddCartItemRequest,
    }): CancelablePromise<ApiResponseVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/cart/items',
            headers: {
                'X-Member-Id': xMemberId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                404: `상품 미존재`,
            },
        });
    }

    /**
     * 장바구니 전체 비우기
     * 회원의 장바구니를 전체 삭제합니다.
     * @returns ApiResponseVoid 요청 성공
     * @throws ApiError
     */
    public static clearCart({
                                xMemberId,
                            }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
    }): CancelablePromise<ApiResponseVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/cart/items',
            headers: {
                'X-Member-Id': xMemberId,
            },
        });
    }

    /**
     * 장바구니 단건 삭제
     * 장바구니에서 특정 상품을 삭제합니다.
     * @returns ApiResponseVoid 요청 성공
     * @throws ApiError
     */
    public static removeCartItem({
                                     xMemberId,
                                     productId,
                                 }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
        /**
         * 상품 ID
         */
        productId: number,
    }): CancelablePromise<ApiResponseVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/cart/items/{productId}',
            path: {
                'productId': productId,
            },
            headers: {
                'X-Member-Id': xMemberId,
            },
        });
    }

    /**
     * 장바구니 수량 변경
     * 장바구니 상품의 수량을 변경합니다.
     * @returns ApiResponseVoid 요청 성공
     * @throws ApiError
     */
    public static updateCartItemQuantity({
                                             xMemberId,
                                             productId,
                                             requestBody,
                                         }: {
        /**
         * 회원 ID 헤더
         */
        xMemberId: number,
        /**
         * 상품 ID
         */
        productId: number,
        requestBody: UpdateCartItemQuantityRequest,
    }): CancelablePromise<ApiResponseVoid> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/cart/items/{productId}',
            path: {
                'productId': productId,
            },
            headers: {
                'X-Member-Id': xMemberId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                404: `상품 미존재`,
            },
        });
    }
}

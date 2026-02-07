/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ApiResponseVoid} from '../models/ApiResponseVoid';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';

export class WishlistService {
    /**
     * 상품 찜 추가
     * 회원이 활성 상품을 찜 목록에 추가합니다. 이미 찜한 경우에도 성공 응답합니다.
     * @returns ApiResponseVoid 요청 성공
     * @throws ApiError
     */
    public static addWishlist({
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
            method: 'POST',
            url: '/api/products/{productId}/wishlist',
            path: {
                'productId': productId,
            },
            headers: {
                'X-Member-Id': xMemberId,
            },
            errors: {
                404: `상품 미존재`,
            },
        });
    }

    /**
     * 상품 찜 해제
     * 회원의 상품 찜을 해제합니다. 이미 찜이 없어도 성공 응답합니다.
     * @returns ApiResponseVoid 요청 성공
     * @throws ApiError
     */
    public static removeWishlist({
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
            url: '/api/products/{productId}/wishlist',
            path: {
                'productId': productId,
            },
            headers: {
                'X-Member-Id': xMemberId,
            },
        });
    }
}

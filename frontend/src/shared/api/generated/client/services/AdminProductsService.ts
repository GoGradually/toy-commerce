/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ApiResponseProductResponse} from '../models/ApiResponseProductResponse';
import type {ApiResponseVoid} from '../models/ApiResponseVoid';
import type {CreateProductRequest} from '../models/CreateProductRequest';
import type {UpdateProductRequest} from '../models/UpdateProductRequest';
import type {UpdateProductStockRequest} from '../models/UpdateProductStockRequest';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';

export class AdminProductsService {
    /**
     * 상품 생성
     * 관리자가 신규 상품을 생성합니다.
     * @returns ApiResponseProductResponse 생성 성공
     * @throws ApiError
     */
    public static createProduct({
                                    requestBody,
                                }: {
        requestBody: CreateProductRequest,
    }): CancelablePromise<ApiResponseProductResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/admin/products',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `잘못된 요청 값`,
            },
        });
    }

    /**
     * 상품 삭제
     * 상품을 삭제합니다.
     * @returns ApiResponseVoid 삭제 성공
     * @throws ApiError
     */
    public static deleteProduct({
                                    productId,
                                }: {
        /**
         * 상품 ID
         */
        productId: number,
    }): CancelablePromise<ApiResponseVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/admin/products/{productId}',
            path: {
                'productId': productId,
            },
            errors: {
                404: `상품 미존재`,
            },
        });
    }

    /**
     * 상품 수정
     * 상품명, 가격, 상태를 수정합니다.
     * @returns ApiResponseProductResponse 수정 성공
     * @throws ApiError
     */
    public static updateProduct({
                                    productId,
                                    requestBody,
                                }: {
        /**
         * 상품 ID
         */
        productId: number,
        requestBody: UpdateProductRequest,
    }): CancelablePromise<ApiResponseProductResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/admin/products/{productId}',
            path: {
                'productId': productId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                404: `상품 미존재`,
            },
        });
    }

    /**
     * 재고 수정
     * 상품 재고 수량을 변경합니다.
     * @returns ApiResponseProductResponse 수정 성공
     * @throws ApiError
     */
    public static updateStock({
                                  productId,
                                  requestBody,
                              }: {
        /**
         * 상품 ID
         */
        productId: number,
        requestBody: UpdateProductStockRequest,
    }): CancelablePromise<ApiResponseProductResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/admin/products/{productId}/stock',
            path: {
                'productId': productId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                404: `상품 미존재`,
            },
        });
    }
}

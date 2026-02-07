/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ApiResponseProductListResponse} from '../models/ApiResponseProductListResponse';
import type {ApiResponseProductResponse} from '../models/ApiResponseProductResponse';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';

export class PublicProductsService {
    /**
     * 상품 목록 조회
     * 활성 상태의 상품 목록을 페이지네이션으로 조회합니다.
     * @returns ApiResponseProductListResponse 조회 성공
     * @throws ApiError
     */
    public static getProducts({
                                  page,
                                  size = 20,
                                  sortBy = 'createdAt',
                                  direction = 'desc',
                              }: {
        /**
         * 페이지 번호(0부터 시작)
         */
        page?: number,
        /**
         * 페이지 크기(1~100)
         */
        size?: number,
        /**
         * 정렬 필드(id, name, price, createdAt)
         */
        sortBy?: string,
        /**
         * 정렬 방향(asc, desc)
         */
        direction?: string,
    }): CancelablePromise<ApiResponseProductListResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/products',
            query: {
                'page': page,
                'size': size,
                'sortBy': sortBy,
                'direction': direction,
            },
            errors: {
                400: `잘못된 요청 값`,
            },
        });
    }

    /**
     * 상품 상세 조회
     * 상품 ID로 활성 상품 상세 정보를 조회합니다.
     * @returns ApiResponseProductResponse 조회 성공
     * @throws ApiError
     */
    public static getProduct({
                                 productId,
                             }: {
        /**
         * 상품 ID
         */
        productId: number,
    }): CancelablePromise<ApiResponseProductResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/products/{productId}',
            path: {
                'productId': productId,
            },
            errors: {
                404: `상품 미존재`,
            },
        });
    }
}

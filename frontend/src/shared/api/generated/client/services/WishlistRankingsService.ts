/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ApiResponseWishlistPopularRankingResponse} from '../models/ApiResponseWishlistPopularRankingResponse';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';

export class WishlistRankingsService {
    /**
     * 인기 찜 랭킹 조회
     * Redis Sorted Set 기준으로 인기 찜 랭킹을 조회합니다.
     * @returns ApiResponseWishlistPopularRankingResponse 조회 성공
     * @throws ApiError
     */
    public static getPopularWishlistRankings({
                                                 limit = 10,
                                             }: {
        /**
         * 반환할 랭킹 개수(1~100)
         */
        limit?: number,
    }): CancelablePromise<ApiResponseWishlistPopularRankingResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/rankings/wishlist/popular',
            query: {
                'limit': limit,
            },
            errors: {
                400: `잘못된 요청 값`,
            },
        });
    }
}

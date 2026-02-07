/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {WishlistPopularRankingItemResponse} from './WishlistPopularRankingItemResponse';

/**
 * 인기 찜 랭킹 응답
 */
export type WishlistPopularRankingResponse = {
    /**
     * 요청 limit
     */
    limit?: number;
    /**
     * 랭킹 목록
     */
    rankings?: Array<WishlistPopularRankingItemResponse>;
};


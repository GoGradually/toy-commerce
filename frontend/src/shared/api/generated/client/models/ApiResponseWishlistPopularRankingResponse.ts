/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ErrorBody} from './ErrorBody';
import type {WishlistPopularRankingResponse} from './WishlistPopularRankingResponse';

/**
 * 공통 API 응답
 */
export type ApiResponseWishlistPopularRankingResponse = {
    /**
     * 요청 성공 여부
     */
    success?: boolean;
    /**
     * 응답 데이터
     */
    data?: WishlistPopularRankingResponse;
    /**
     * 에러 정보
     */
    error?: ErrorBody;
};


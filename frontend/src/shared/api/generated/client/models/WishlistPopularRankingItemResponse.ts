/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 인기 찜 랭킹 항목
 */
export type WishlistPopularRankingItemResponse = {
    /**
     * 랭킹 순위
     */
    rank?: number;
    /**
     * 상품 ID
     */
    productId?: number;
    /**
     * 상품명
     */
    name?: string;
    /**
     * 가격
     */
    price?: number;
    /**
     * 상품 상태
     */
    status?: 'ACTIVE' | 'INACTIVE';
    /**
     * 찜 수
     */
    wishlistCount?: number;
};


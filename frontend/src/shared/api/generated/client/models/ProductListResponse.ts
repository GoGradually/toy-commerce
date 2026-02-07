/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ProductResponse} from './ProductResponse';

/**
 * 상품 목록 응답
 */
export type ProductListResponse = {
    /**
     * 상품 목록
     */
    products?: Array<ProductResponse>;
    /**
     * 현재 페이지
     */
    page?: number;
    /**
     * 페이지 크기
     */
    size?: number;
    /**
     * 전체 데이터 수
     */
    totalElements?: number;
    /**
     * 전체 페이지 수
     */
    totalPages?: number;
    /**
     * 다음 페이지 존재 여부
     */
    hasNext?: boolean;
};


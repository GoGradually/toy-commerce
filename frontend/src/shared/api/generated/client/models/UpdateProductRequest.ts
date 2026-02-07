/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 상품 수정 요청
 */
export type UpdateProductRequest = {
    /**
     * 상품명
     */
    name: string;
    /**
     * 가격
     */
    price: number;
    /**
     * 상품 상태
     */
    status: 'ACTIVE' | 'INACTIVE';
};


/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 상품 생성 요청
 */
export type CreateProductRequest = {
    /**
     * 상품명
     */
    name: string;
    /**
     * 가격
     */
    price: number;
    /**
     * 재고 수량
     */
    stock: number;
    /**
     * 상품 상태
     */
    status: 'ACTIVE' | 'INACTIVE';
};


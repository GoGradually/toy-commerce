/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {ErrorBody} from './ErrorBody';
import type {ProductResponse} from './ProductResponse';

/**
 * 공통 API 응답
 */
export type ApiResponseProductResponse = {
    /**
     * 요청 성공 여부
     */
    success?: boolean;
    /**
     * 응답 데이터
     */
    data?: ProductResponse;
    /**
     * 에러 정보
     */
    error?: ErrorBody;
};


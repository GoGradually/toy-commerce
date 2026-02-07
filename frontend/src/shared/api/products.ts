import {PublicProductsService} from './generated/client';
import {normalizeClientError, unwrapApiEnvelope} from './core';
import type {ProductListResponse, ProductResponse} from './generated/schema';

export interface ProductListQuery {
    page: number;
    size: number;
    sortBy: string;
    direction: 'asc' | 'desc';
}

export async function getProducts(query: ProductListQuery): Promise<ProductListResponse> {
    try {
        const response = await PublicProductsService.getProducts({
            page: query.page,
            size: query.size,
            sortBy: query.sortBy,
            direction: query.direction
        });
        return unwrapApiEnvelope(response) as ProductListResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function getProduct(productId: number): Promise<ProductResponse> {
    try {
        const response = await PublicProductsService.getProduct({productId});
        return unwrapApiEnvelope(response) as ProductResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

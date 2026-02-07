import {AdminProductsService} from './generated/client';
import {normalizeClientError, unwrapApiEnvelope} from './core';
import type {
  CreateProductRequest,
  ProductResponse,
  UpdateProductRequest,
  UpdateProductStockRequest
} from './generated/schema';

export async function createProduct(request: CreateProductRequest): Promise<ProductResponse> {
    try {
        const response = await AdminProductsService.createProduct({
            requestBody: request
        });
        return unwrapApiEnvelope(response) as ProductResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function updateProduct(productId: number, request: UpdateProductRequest): Promise<ProductResponse> {
    try {
        const response = await AdminProductsService.updateProduct({
            productId,
            requestBody: request
        });
        return unwrapApiEnvelope(response) as ProductResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function updateProductStock(productId: number, request: UpdateProductStockRequest): Promise<ProductResponse> {
    try {
        const response = await AdminProductsService.updateStock({
            productId,
            requestBody: request
        });
        return unwrapApiEnvelope(response) as ProductResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function deleteProduct(productId: number): Promise<void> {
    try {
        const response = await AdminProductsService.deleteProduct({productId});
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

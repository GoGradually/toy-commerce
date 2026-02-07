import {describe, expect, it, vi} from 'vitest';
import {ApiClientError, apiRequest} from './core';

describe('apiRequest', () => {
    it('returns data when envelope success is true', async () => {
        vi.stubGlobal(
            'fetch',
            vi.fn(async () =>
                new Response(JSON.stringify({success: true, data: {id: 1}, error: null}), {
                    status: 200,
                    headers: {'Content-Type': 'application/json'}
                })
            )
        );

        const data = await apiRequest<{ id: number }>('/api/products/1');

        expect(data.id).toBe(1);
    });

    it('throws ApiClientError with backend code when status is not ok', async () => {
        vi.stubGlobal(
            'fetch',
            vi.fn(async () =>
                new Response(
                    JSON.stringify({
                        success: false,
                        data: null,
                        error: {code: 'PRODUCT-404', message: 'Product not found'}
                    }),
                    {
                        status: 404,
                        headers: {'Content-Type': 'application/json'}
                    }
                )
            )
        );

        await expect(apiRequest('/api/products/999')).rejects.toMatchObject({
            name: 'ApiClientError',
            code: 'PRODUCT-404'
        });
    });

    it('throws ApiClientError when success is false in a 200 response', async () => {
        vi.stubGlobal(
            'fetch',
            vi.fn(async () =>
                new Response(
                    JSON.stringify({
                        success: false,
                        data: null,
                        error: {code: 'COMMON-400', message: 'Invalid request'}
                    }),
                    {
                        status: 200,
                        headers: {'Content-Type': 'application/json'}
                    }
                )
            )
        );

        await expect(apiRequest('/api/products')).rejects.toBeInstanceOf(ApiClientError);
    });
});

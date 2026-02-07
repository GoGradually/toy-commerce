import {describe, expect, it} from 'vitest';
import {parseProductListParams, withProductListSearchParams} from './product-list-params';

describe('parseProductListParams', () => {
    it('returns defaults when params are absent', () => {
        const params = parseProductListParams(new URLSearchParams());

        expect(params).toEqual({
            page: 0,
            size: 20,
            sortBy: 'createdAt',
            direction: 'desc'
        });
    });

    it('parses query params and normalizes direction', () => {
        const params = parseProductListParams(
            new URLSearchParams({
                page: '2',
                size: '50',
                sortBy: 'price',
                direction: 'asc'
            })
        );

        expect(params).toEqual({
            page: 2,
            size: 50,
            sortBy: 'price',
            direction: 'asc'
        });
    });

    it('falls back to default direction for unknown value', () => {
        const params = parseProductListParams(new URLSearchParams({direction: 'up'}));
        expect(params.direction).toBe('desc');
    });
});

describe('withProductListSearchParams', () => {
    it('updates sort and resets page when requested', () => {
        const current = new URLSearchParams({page: '4', size: '20', sortBy: 'createdAt', direction: 'desc'});
        const next = withProductListSearchParams(current, {sortBy: 'name', resetPage: true});

        expect(next.get('sortBy')).toBe('name');
        expect(next.get('page')).toBe('0');
        expect(next.get('size')).toBe('20');
    });

    it('updates page without dropping other params', () => {
        const current = new URLSearchParams({page: '1', size: '20', sortBy: 'id', direction: 'desc'});
        const next = withProductListSearchParams(current, {page: 2});

        expect(next.toString()).toContain('page=2');
        expect(next.toString()).toContain('sortBy=id');
    });
});

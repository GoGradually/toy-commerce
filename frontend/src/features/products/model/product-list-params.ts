import {parseIntegerParam} from '../../common/model/number-param';

export const productSortFields = ['createdAt', 'id', 'name', 'price'] as const;

export type ProductSortDirection = 'asc' | 'desc';

export interface ProductListParams {
    page: number;
    size: number;
    sortBy: string;
    direction: ProductSortDirection;
}

export function parseProductListParams(searchParams: URLSearchParams): ProductListParams {
    const page = parseIntegerParam(searchParams.get('page'), 0);
    const size = parseIntegerParam(searchParams.get('size'), 20);
    const sortBy = searchParams.get('sortBy') ?? 'createdAt';
    const direction = searchParams.get('direction') === 'asc' ? 'asc' : 'desc';

    return {page, size, sortBy, direction};
}

export function withProductListSearchParams(
    current: URLSearchParams,
    updates: Partial<ProductListParams> & { resetPage?: boolean }
): URLSearchParams {
    const next = new URLSearchParams(current);

    if (updates.sortBy !== undefined) {
        next.set('sortBy', updates.sortBy);
    }

    if (updates.direction !== undefined) {
        next.set('direction', updates.direction);
    }

    if (updates.page !== undefined) {
        next.set('page', String(updates.page));
    }

    if (updates.size !== undefined) {
        next.set('size', String(updates.size));
    }

    if (updates.resetPage) {
        next.set('page', '0');
    }

    return next;
}

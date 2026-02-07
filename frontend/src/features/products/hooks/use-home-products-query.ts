import {useQuery} from '@tanstack/react-query';
import {getProducts} from '../../../shared/api/products';
import {queryKeys} from '../../../shared/api/query-keys';
import type {ProductListParams} from '../model/product-list-params';

export function useHomeProductsQuery(params: ProductListParams) {
    return useQuery({
        queryKey: queryKeys.products.list(params.page, params.size, params.sortBy, params.direction),
        queryFn: () => getProducts(params)
    });
}

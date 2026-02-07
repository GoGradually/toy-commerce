import {useCallback} from 'react';
import {useQueryClient} from '@tanstack/react-query';
import {queryKeys} from '../../../shared/api/query-keys';

export function useAdminProductInvalidations(productId: number) {
    const queryClient = useQueryClient();

    return useCallback(() => {
        queryClient.invalidateQueries({queryKey: queryKeys.admin.products()});
        queryClient.invalidateQueries({queryKey: queryKeys.products.list(0, 20, 'createdAt', 'desc')});

        if (Number.isFinite(productId) && productId >= 1) {
            queryClient.invalidateQueries({queryKey: queryKeys.products.detail(productId)});
        }
    }, [productId, queryClient]);
}

import {useMutation} from '@tanstack/react-query';
import {deleteProduct, updateProduct, updateProductStock} from '../../../shared/api/admin-products';
import type {UpdateProductRequest, UpdateProductStockRequest} from '../../../shared/api/generated/schema';

interface UseAdminProductActionsOptions {
    productId: number;
    invalidateAll: () => void;
    onDeleteSuccess: () => void;
}

export function useAdminProductActions({
                                           productId,
                                           invalidateAll,
                                           onDeleteSuccess
                                       }: UseAdminProductActionsOptions) {
    const updateMutation = useMutation({
        mutationFn: (request: UpdateProductRequest) => updateProduct(productId, request),
        onSuccess: invalidateAll
    });

    const stockMutation = useMutation({
        mutationFn: (request: UpdateProductStockRequest) => updateProductStock(productId, request),
        onSuccess: invalidateAll
    });

    const deleteMutation = useMutation({
        mutationFn: () => deleteProduct(productId),
        onSuccess: () => {
            invalidateAll();
            onDeleteSuccess();
        }
    });

    return {
        updateMutation,
        stockMutation,
        deleteMutation,
        submitUpdate: (request: UpdateProductRequest) => updateMutation.mutate(request),
        submitStock: (request: UpdateProductStockRequest) => stockMutation.mutate(request),
        submitDelete: () => deleteMutation.mutate()
    };
}

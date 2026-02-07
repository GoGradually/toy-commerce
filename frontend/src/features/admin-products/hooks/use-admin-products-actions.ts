import {useMemo} from 'react';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useNavigate} from 'react-router-dom';
import {deleteProduct} from '../../../shared/api/admin-products';
import {queryKeys} from '../../../shared/api/query-keys';
import {firstErrorMessage} from '../../common/model/error';
import {parsePositiveIntOrNull} from '../../common/model/number-param';

export function useAdminProductsActions() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const deleteMutation = useMutation({
        mutationFn: (productId: number) => deleteProduct(productId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.admin.products()});
            queryClient.invalidateQueries({queryKey: queryKeys.products.list(0, 20, 'createdAt', 'desc')});
        }
    });

    const deleteError = useMemo(
        () => firstErrorMessage([deleteMutation.error]),
        [deleteMutation.error]
    );

    return {
        deleteError,
        deleteById: (productId: number) => deleteMutation.mutate(productId),
        navigateToEditByInput: (rawProductId: string) => {
            const productId = parsePositiveIntOrNull(rawProductId);
            if (productId !== null) {
                navigate(`/admin/products/${productId}/edit`);
            }
        },
        deleteByInput: (rawProductId: string) => {
            const productId = parsePositiveIntOrNull(rawProductId);
            if (productId !== null) {
                deleteMutation.mutate(productId);
            }
        }
    };
}

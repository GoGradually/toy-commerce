import {useMemo} from 'react';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {addCartItem, clearCart, removeCartItem, updateCartItemQuantity} from '../../../shared/api/cart';
import {checkoutOrder} from '../../../shared/api/orders';
import {queryKeys} from '../../../shared/api/query-keys';
import {firstErrorMessage} from '../../common/model/error';

interface UseCartActionsOptions {
    memberId: number;
    onCheckoutSuccess: (orderId: number) => void;
}

export function useCartActions({memberId, onCheckoutSuccess}: UseCartActionsOptions) {
    const queryClient = useQueryClient();

    const refreshCart = () => queryClient.invalidateQueries({queryKey: queryKeys.cart.items(memberId)});

    const addMutation = useMutation({
        mutationFn: (request: { productId: number; quantity: number }) => addCartItem(memberId, request),
        onSuccess: refreshCart
    });

    const updateMutation = useMutation({
        mutationFn: (request: { productId: number; quantity: number }) =>
            updateCartItemQuantity(memberId, request.productId, {quantity: request.quantity}),
        onSuccess: refreshCart
    });

    const removeMutation = useMutation({
        mutationFn: (productId: number) => removeCartItem(memberId, productId),
        onSuccess: refreshCart
    });

    const clearMutation = useMutation({
        mutationFn: () => clearCart(memberId),
        onSuccess: refreshCart
    });

    const checkoutMutation = useMutation({
        mutationFn: () => checkoutOrder(memberId),
        onSuccess: (response) => {
            refreshCart();
            onCheckoutSuccess(response.orderId);
        }
    });

    const mutationError = useMemo(
        () =>
            firstErrorMessage([
                addMutation.error,
                updateMutation.error,
                removeMutation.error,
                clearMutation.error,
                checkoutMutation.error
            ]),
        [addMutation.error, checkoutMutation.error, clearMutation.error, removeMutation.error, updateMutation.error]
    );

    return {
        mutationError,
        addItem: (request: { productId: number; quantity: number }) => addMutation.mutate(request),
        updateItemQuantity: (request: { productId: number; quantity: number }) => updateMutation.mutate(request),
        removeItem: (productId: number) => removeMutation.mutate(productId),
        clearItems: () => clearMutation.mutate(),
        checkout: () => checkoutMutation.mutate()
    };
}

import {useMemo} from 'react';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {addCartItem} from '../../../shared/api/cart';
import {queryKeys} from '../../../shared/api/query-keys';
import {addWishlist, removeWishlist} from '../../../shared/api/wishlist';
import {firstErrorMessage} from '../../common/model/error';

export function useProductDetailActions(memberId: number, productId: number) {
    const queryClient = useQueryClient();

    const addCartMutation = useMutation({
        mutationFn: () => addCartItem(memberId, {productId, quantity: 1}),
        onSuccess: () => queryClient.invalidateQueries({queryKey: queryKeys.cart.items(memberId)})
    });

    const addWishlistMutation = useMutation({
        mutationFn: () => addWishlist(productId, memberId),
        onSuccess: () => queryClient.invalidateQueries({queryKey: queryKeys.wishlist.ranking(10)})
    });

    const removeWishlistMutation = useMutation({
        mutationFn: () => removeWishlist(productId, memberId),
        onSuccess: () => queryClient.invalidateQueries({queryKey: queryKeys.wishlist.ranking(10)})
    });

    const errors = useMemo(
        () => ({
            addToCart: firstErrorMessage([addCartMutation.error]),
            addWishlist: firstErrorMessage([addWishlistMutation.error]),
            removeWishlist: firstErrorMessage([removeWishlistMutation.error])
        }),
        [addCartMutation.error, addWishlistMutation.error, removeWishlistMutation.error]
    );

    return {
        addToCart: () => addCartMutation.mutate(),
        addToWishlist: () => addWishlistMutation.mutate(),
        removeWishlist: () => removeWishlistMutation.mutate(),
        errors
    };
}

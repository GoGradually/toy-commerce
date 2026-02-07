import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useState} from 'react';
import {addCartItem} from '../../../shared/api/cart';
import {toErrorMessage} from '../../../shared/api/core';
import {queryKeys} from '../../../shared/api/query-keys';
import {addWishlist, removeWishlist} from '../../../shared/api/wishlist';

export function useHomeProductActions(memberId: number) {
    const queryClient = useQueryClient();
    const [feedback, setFeedback] = useState<string | null>(null);

    const addCartMutation = useMutation({
        mutationFn: (productId: number) =>
            addCartItem(memberId, {
                productId,
                quantity: 1
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.cart.items(memberId)});
            setFeedback('장바구니에 담았습니다.');
        },
        onError: (error) => setFeedback(toErrorMessage(error))
    });

    const addWishlistMutation = useMutation({
        mutationFn: (productId: number) => addWishlist(productId, memberId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.wishlist.ranking(10)});
            setFeedback('위시리스트를 추가했습니다.');
        },
        onError: (error) => setFeedback(toErrorMessage(error))
    });

    const removeWishlistMutation = useMutation({
        mutationFn: (productId: number) => removeWishlist(productId, memberId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.wishlist.ranking(10)});
            setFeedback('위시리스트를 제거했습니다.');
        },
        onError: (error) => setFeedback(toErrorMessage(error))
    });

    return {
        feedback,
        addToCart: (productId: number) => addCartMutation.mutate(productId),
        addToWishlist: (productId: number) => addWishlistMutation.mutate(productId),
        removeFromWishlist: (productId: number) => removeWishlistMutation.mutate(productId)
    };
}

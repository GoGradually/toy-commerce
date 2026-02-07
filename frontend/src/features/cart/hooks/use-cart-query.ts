import {useQuery} from '@tanstack/react-query';
import {getCart} from '../../../shared/api/cart';
import {queryKeys} from '../../../shared/api/query-keys';

export function useCartQuery(memberId: number) {
    return useQuery({
        queryKey: queryKeys.cart.items(memberId),
        queryFn: () => getCart(memberId)
    });
}

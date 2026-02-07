export const queryKeys = {
    products: {
        list: (page: number, size: number, sortBy: string, direction: string) =>
            ['products', 'list', page, size, sortBy, direction] as const,
        detail: (productId: number) => ['products', 'detail', productId] as const
    },
    wishlist: {
        ranking: (limit: number) => ['wishlist', 'ranking', limit] as const
    },
    cart: {
        items: (memberId: number) => ['cart', 'items', memberId] as const
    },
    orders: {
        detail: (memberId: number, orderId: number) => ['orders', 'detail', memberId, orderId] as const
    },
    admin: {
        products: () => ['admin', 'products'] as const
    }
};

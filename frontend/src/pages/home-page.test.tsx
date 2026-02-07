import {screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {renderPage} from '../test/render-page';
import {ApiClientError} from '../shared/api/core';
import {HomePage} from './home-page';
import {addCartItem} from '../shared/api/cart';
import {getProducts} from '../shared/api/products';
import {addWishlist, removeWishlist} from '../shared/api/wishlist';

vi.mock('../shared/api/products', () => ({
    getProducts: vi.fn()
}));

vi.mock('../shared/api/cart', () => ({
    addCartItem: vi.fn()
}));

vi.mock('../shared/api/wishlist', () => ({
    addWishlist: vi.fn(),
    removeWishlist: vi.fn()
}));

const sampleProducts = {
    products: [
        {
            id: 1,
            name: 'Robot Car',
            price: 15000,
            stock: 12,
            status: 'ACTIVE' as const
        }
    ],
    page: 0,
    size: 20,
    totalElements: 1,
    totalPages: 1,
    hasNext: false
};

describe('HomePage', () => {
    beforeEach(() => {
        vi.mocked(getProducts).mockReset();
        vi.mocked(addCartItem).mockReset();
        vi.mocked(addWishlist).mockReset();
        vi.mocked(removeWishlist).mockReset();

        vi.mocked(getProducts).mockResolvedValue(sampleProducts);
        vi.mocked(addCartItem).mockResolvedValue(undefined);
        vi.mocked(addWishlist).mockResolvedValue(undefined);
        vi.mocked(removeWishlist).mockResolvedValue(undefined);
    });

    it('loads products and adds item to cart', async () => {
        const user = userEvent.setup();

        renderPage({
            path: '/',
            element: <HomePage/>,
            initialEntry: '/'
        });

        await screen.findByText('Robot Car');
        await user.click(screen.getByRole('button', {name: '장바구니 추가'}));

        await screen.findByText('장바구니에 담았습니다.');
        expect(addCartItem).toHaveBeenCalledWith(1, {productId: 1, quantity: 1});
    });

    it('renders error panel when product query fails', async () => {
        vi.mocked(getProducts).mockRejectedValueOnce(new ApiClientError('Product not found', 404, 'PRODUCT-404'));

        renderPage({
            path: '/',
            element: <HomePage/>,
            initialEntry: '/'
        });

        await screen.findByText('상품을 불러오지 못했습니다');
        expect(screen.getByText('PRODUCT-404: Product not found')).toBeInTheDocument();
    });

    it('updates sort query param and refetches with page reset', async () => {
        const user = userEvent.setup();

        renderPage({
            path: '/',
            element: <HomePage/>,
            initialEntry: '/?page=3&size=20&sortBy=createdAt&direction=desc'
        });

        await screen.findByText('Robot Car');

        const [sortSelect] = screen.getAllByRole('combobox');
        await user.selectOptions(sortSelect, 'price');

        await waitFor(() => {
            expect(getProducts).toHaveBeenLastCalledWith({
                page: 0,
                size: 20,
                sortBy: 'price',
                direction: 'desc'
            });
        });
    });
});

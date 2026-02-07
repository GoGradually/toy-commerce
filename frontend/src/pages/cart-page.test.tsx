import {fireEvent, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {useParams} from 'react-router-dom';
import {renderPage} from '../test/render-page';
import {CartPage} from './cart-page';
import {addCartItem, clearCart, getCart, removeCartItem, updateCartItemQuantity} from '../shared/api/cart';
import {checkoutOrder} from '../shared/api/orders';

vi.mock('../shared/api/cart', () => ({
    getCart: vi.fn(),
    addCartItem: vi.fn(),
    updateCartItemQuantity: vi.fn(),
    removeCartItem: vi.fn(),
    clearCart: vi.fn()
}));

vi.mock('../shared/api/orders', () => ({
    checkoutOrder: vi.fn()
}));

function OrderPageProbe() {
    const {orderId} = useParams();
    return <p>Order {orderId} page</p>;
}

describe('CartPage', () => {
    beforeEach(() => {
        vi.mocked(getCart).mockReset();
        vi.mocked(addCartItem).mockReset();
        vi.mocked(updateCartItemQuantity).mockReset();
        vi.mocked(removeCartItem).mockReset();
        vi.mocked(clearCart).mockReset();
        vi.mocked(checkoutOrder).mockReset();

        vi.mocked(addCartItem).mockResolvedValue(undefined);
        vi.mocked(updateCartItemQuantity).mockResolvedValue(undefined);
        vi.mocked(removeCartItem).mockResolvedValue(undefined);
        vi.mocked(clearCart).mockResolvedValue(undefined);
        vi.mocked(checkoutOrder).mockResolvedValue({
            orderId: 42,
            status: 'PENDING_PAYMENT',
            totalAmount: 1000,
            items: []
        });
    });

    it('adds an item from quick-add form', async () => {
        const user = userEvent.setup();
        vi.mocked(getCart).mockResolvedValue({items: [], cartTotal: 0});

        renderPage({
            path: '/cart',
            element: <CartPage/>,
            initialEntry: '/cart'
        });

        await screen.findByText('장바구니가 비어 있습니다');

        await user.type(screen.getByPlaceholderText('상품ID'), '3');
        await user.clear(screen.getByPlaceholderText('수량'));
        await user.type(screen.getByPlaceholderText('수량'), '2');
        await user.click(screen.getByRole('button', {name: '상품 추가'}));

        await waitFor(() => {
            expect(addCartItem).toHaveBeenCalledWith(1, {productId: 3, quantity: 2});
        });
    });

    it('updates quantity, removes item, and clears cart', async () => {
        const user = userEvent.setup();
        vi.mocked(getCart).mockResolvedValue({
            items: [
                {
                    productId: 10,
                    name: 'Robot',
                    price: 1000,
                    quantity: 4,
                    lineTotal: 4000
                }
            ],
            cartTotal: 4000
        });

        renderPage({
            path: '/cart',
            element: <CartPage/>,
            initialEntry: '/cart'
        });

        await screen.findByText('Robot');

        const quantityInput = screen.getByDisplayValue('4');
        fireEvent.change(quantityInput, {target: {value: '5'}});

        await waitFor(() => {
            expect(updateCartItemQuantity).toHaveBeenCalledWith(1, 10, {quantity: 5});
        });

        await user.click(screen.getByRole('button', {name: '삭제'}));
        await user.click(screen.getByRole('button', {name: '장바구니 비우기'}));

        expect(removeCartItem).toHaveBeenCalledWith(1, 10);
        expect(clearCart).toHaveBeenCalledWith(1);
    });

    it('navigates to order detail after successful checkout', async () => {
        const user = userEvent.setup();
        vi.mocked(getCart).mockResolvedValue({
            items: [
                {
                    productId: 10,
                    name: 'Robot',
                    price: 1000,
                    quantity: 1,
                    lineTotal: 1000
                }
            ],
            cartTotal: 1000
        });

        renderPage({
            path: '/cart',
            element: <CartPage/>,
            initialEntry: '/cart',
            extraRoutes: [{path: '/orders/:orderId', element: <OrderPageProbe/>}]
        });

        await screen.findByText('Robot');
        await user.click(screen.getByRole('button', {name: '주문하기'}));

        await screen.findByText('Order 42 page');
        expect(checkoutOrder).toHaveBeenCalledWith(1);
    });
});

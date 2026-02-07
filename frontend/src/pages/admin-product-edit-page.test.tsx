import {screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {renderPage} from '../test/render-page';
import {deleteProduct, updateProduct, updateProductStock} from '../shared/api/admin-products';
import {getProduct} from '../shared/api/products';
import {AdminProductEditPage} from './admin-product-edit-page';

vi.mock('../shared/api/products', () => ({
    getProduct: vi.fn()
}));

vi.mock('../shared/api/admin-products', () => ({
    updateProduct: vi.fn(),
    updateProductStock: vi.fn(),
    deleteProduct: vi.fn()
}));

const sampleProduct = {
    id: 7,
    name: 'Starter Toy',
    price: 1900,
    stock: 8,
    status: 'ACTIVE' as const
};

describe('AdminProductEditPage', () => {
    beforeEach(() => {
        vi.mocked(getProduct).mockReset();
        vi.mocked(updateProduct).mockReset();
        vi.mocked(updateProductStock).mockReset();
        vi.mocked(deleteProduct).mockReset();

        vi.mocked(getProduct).mockResolvedValue(sampleProduct);
        vi.mocked(updateProduct).mockResolvedValue({...sampleProduct, name: 'Updated Toy'});
        vi.mocked(updateProductStock).mockResolvedValue({...sampleProduct, stock: 99});
        vi.mocked(deleteProduct).mockResolvedValue(undefined);
    });

    it('loads product details into form', async () => {
        renderPage({
            path: '/admin/products/:productId/edit',
            element: <AdminProductEditPage/>,
            initialEntry: '/admin/products/7/edit'
        });

        await screen.findByDisplayValue('Starter Toy');

        expect(getProduct).toHaveBeenCalledWith(7);
        expect(screen.getByDisplayValue('1900')).toBeInTheDocument();
        expect(screen.getByDisplayValue('8')).toBeInTheDocument();
    });

    it('submits product and stock updates', async () => {
        const user = userEvent.setup();

        renderPage({
            path: '/admin/products/:productId/edit',
            element: <AdminProductEditPage/>,
            initialEntry: '/admin/products/7/edit'
        });

        const nameInput = await screen.findByDisplayValue('Starter Toy');
        const priceInput = screen.getByDisplayValue('1900');
        const stockInput = screen.getByDisplayValue('8');

        await user.clear(nameInput);
        await user.type(nameInput, 'Updated Robot');
        await user.clear(priceInput);
        await user.type(priceInput, '2100');
        await user.click(screen.getByRole('button', {name: '상품 정보 저장'}));

        await waitFor(() => {
            expect(updateProduct).toHaveBeenCalledWith(7, {
                name: 'Updated Robot',
                price: 2100,
                status: 'ACTIVE'
            });
        });

        await user.clear(stockInput);
        await user.type(stockInput, '99');
        await user.click(screen.getByRole('button', {name: '재고 수정'}));

        await waitFor(() => {
            expect(updateProductStock).toHaveBeenCalledWith(7, {stock: 99});
        });
    });

    it('deletes product and navigates to admin list', async () => {
        const user = userEvent.setup();

        renderPage({
            path: '/admin/products/:productId/edit',
            element: <AdminProductEditPage/>,
            initialEntry: '/admin/products/7/edit',
            extraRoutes: [{path: '/admin/products', element: <p>관리자 목록 페이지</p>}]
        });

        await screen.findByDisplayValue('Starter Toy');
        await user.click(screen.getByRole('button', {name: '삭제'}));

        await screen.findByText('관리자 목록 페이지');
        expect(deleteProduct).toHaveBeenCalledWith(7);
    });
});

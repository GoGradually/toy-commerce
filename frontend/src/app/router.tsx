import {createBrowserRouter} from 'react-router-dom';
import {AppShell} from './layout/app-shell';
import {AdminProductCreatePage} from '../pages/admin-product-create-page';
import {AdminProductEditPage} from '../pages/admin-product-edit-page';
import {AdminProductsPage} from '../pages/admin-products-page';
import {CartPage} from '../pages/cart-page';
import {HomePage} from '../pages/home-page';
import {NotFoundPage} from '../pages/not-found-page';
import {OrderDetailPage} from '../pages/order-detail-page';
import {ProductDetailPage} from '../pages/product-detail-page';
import {WishlistRankingPage} from '../pages/wishlist-ranking-page';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <AppShell/>,
        children: [
            {index: true, element: <HomePage/>},
            {path: 'products/:productId', element: <ProductDetailPage/>},
            {path: 'wishlist/ranking', element: <WishlistRankingPage/>},
            {path: 'cart', element: <CartPage/>},
            {path: 'orders/:orderId', element: <OrderDetailPage/>},
            {path: 'admin/products', element: <AdminProductsPage/>},
            {path: 'admin/products/new', element: <AdminProductCreatePage/>},
            {path: 'admin/products/:productId/edit', element: <AdminProductEditPage/>},
            {path: '*', element: <NotFoundPage/>}
        ]
    }
]);

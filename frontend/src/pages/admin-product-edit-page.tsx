import {useQuery} from '@tanstack/react-query';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {useAdminProductActions} from '../features/admin-products/hooks/use-admin-product-actions';
import {useAdminProductForm} from '../features/admin-products/hooks/use-admin-product-form';
import {useAdminProductInvalidations} from '../features/admin-products/hooks/use-admin-product-invalidations';
import {AdminProductEditForm} from '../features/admin-products/ui/admin-product-edit-form';
import {AdminProductStockForm} from '../features/admin-products/ui/admin-product-stock-form';
import {parsePositiveIntOrNull} from '../features/common/model/number-param';
import {toErrorMessage} from '../shared/api/core';
import {getProduct} from '../shared/api/products';
import {queryKeys} from '../shared/api/query-keys';
import {Button} from '../shared/ui/button';
import {StatePanel} from '../shared/ui/state-panel';

export function AdminProductEditPage() {
    const navigate = useNavigate();
    const {productId: rawProductId} = useParams();

    const productId = parsePositiveIntOrNull(rawProductId ?? '');
    const safeProductId = productId ?? 0;
    const invalidateAll = useAdminProductInvalidations(safeProductId);

    const productQuery = useQuery({
        queryKey: queryKeys.products.detail(safeProductId),
        queryFn: () => getProduct(safeProductId),
        enabled: productId !== null
    });

    const form = useAdminProductForm(productQuery.data);
    const actions = useAdminProductActions({
        productId: safeProductId,
        invalidateAll,
        onDeleteSuccess: () => navigate('/admin/products')
    });

    if (productId === null) {
        return <StatePanel title="잘못된 상품 ID입니다"/>;
    }

    return (
        <div className="space-y-4">
            <section
                className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <div>
                    <h2 className="text-xl font-bold">상품 #{productId} 수정</h2>
                    <p className="text-sm text-slate-600">PATCH /api/admin/products/{'{'}productId{'}'}</p>
                </div>
                <div className="flex gap-2">
                    <Link to="/admin/products">
                        <Button variant="secondary">관리자 목록으로</Button>
                    </Link>
                    <Button variant="danger" onClick={actions.submitDelete}>
                        삭제
                    </Button>
                </div>
            </section>

            {productQuery.isLoading ? <StatePanel title="현재 상품 데이터를 불러오는 중..."/> : null}
            {productQuery.isError ? (
                <StatePanel
                    title="상품 상세를 불러오지 못했습니다"
                    description={`${toErrorMessage(productQuery.error)} 값을 직접 입력한 뒤 수정할 수 있습니다.`}
                />
            ) : null}

            <AdminProductEditForm
                name={form.values.name}
                price={form.values.price}
                status={form.values.status}
                onNameChange={form.setName}
                onPriceChange={form.setPrice}
                onStatusChange={form.setStatus}
                onSubmit={() => actions.submitUpdate(form.buildProductRequest())}
            />

            <AdminProductStockForm
                stock={form.values.stock}
                onStockChange={form.setStock}
                onSubmit={() => actions.submitStock(form.buildStockRequest())}
            />

            {actions.updateMutation.isError ?
                <p className="text-sm text-red-700">{toErrorMessage(actions.updateMutation.error)}</p> : null}
            {actions.stockMutation.isError ?
                <p className="text-sm text-red-700">{toErrorMessage(actions.stockMutation.error)}</p> : null}
            {actions.deleteMutation.isError ?
                <p className="text-sm text-red-700">{toErrorMessage(actions.deleteMutation.error)}</p> : null}
            {actions.updateMutation.isSuccess ? <p className="text-sm text-emerald-700">상품이 수정되었습니다.</p> : null}
            {actions.stockMutation.isSuccess ? <p className="text-sm text-emerald-700">재고가 수정되었습니다.</p> : null}
        </div>
    );
}

import {useQuery} from '@tanstack/react-query';
import {useState} from 'react';
import {Link} from 'react-router-dom';
import {useAdminProductsActions} from '../features/admin-products/hooks/use-admin-products-actions';
import {getProducts} from '../shared/api/products';
import {queryKeys} from '../shared/api/query-keys';
import {toErrorMessage} from '../shared/api/core';
import {formatMoney} from '../shared/lib/format';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {Input} from '../shared/ui/input';
import {StatePanel} from '../shared/ui/state-panel';
import {StatusBadge} from '../shared/ui/status-badge';

export function AdminProductsPage() {
    const [productIdInput, setProductIdInput] = useState('');
    const actions = useAdminProductsActions();

    const productQuery = useQuery({
        queryKey: queryKeys.admin.products(),
        queryFn: () => getProducts({page: 0, size: 100, sortBy: 'createdAt', direction: 'desc'})
    });

    const products = productQuery.data?.products ?? [];

    return (
        <div className="space-y-4">
            <section
                className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <div>
                    <h2 className="text-xl font-bold">관리자 상품 센터</h2>
                    <p className="text-sm text-slate-600">관리자 API로 상품과 재고를 관리합니다.</p>
                </div>
                <Link to="/admin/products/new">
                    <Button variant="primary">상품 등록</Button>
                </Link>
            </section>

            <Card className="space-y-3">
                <h3 className="text-lg font-semibold">상품 ID로 빠른 이동</h3>
                <div className="flex flex-wrap gap-2">
                    <Input
                        className="w-40"
                        min={1}
                        placeholder="상품ID"
                        type="number"
                        value={productIdInput}
                        onChange={(event) => setProductIdInput(event.target.value)}
                    />
                    <Button variant="secondary" onClick={() => actions.navigateToEditByInput(productIdInput)}>
                        수정 페이지 열기
                    </Button>
                    <Button variant="danger" onClick={() => actions.deleteByInput(productIdInput)}>
                        ID로 삭제
                    </Button>
                </div>
                {actions.deleteError ? <p className="text-sm text-red-700">{actions.deleteError}</p> : null}
            </Card>

            {productQuery.isLoading ? <StatePanel title="활성 상품을 불러오는 중..."/> : null}
            {productQuery.isError ? (
                <StatePanel title="상품 목록을 불러오지 못했습니다" description={toErrorMessage(productQuery.error)}/>
            ) : null}

            {!productQuery.isLoading && !productQuery.isError ? (
                products.length === 0 ? (
                    <StatePanel title="활성 상품이 없습니다" description="관리자 화면에서 첫 상품을 등록해 주세요."/>
                ) : (
                    <Card className="overflow-x-auto p-0">
                        <table className="min-w-full text-left text-sm">
                            <thead className="bg-slate-50 text-xs uppercase tracking-[0.12em] text-slate-500">
                            <tr>
                                <th className="px-4 py-3">ID</th>
                                <th className="px-4 py-3">이름</th>
                                <th className="px-4 py-3">가격</th>
                                <th className="px-4 py-3">재고</th>
                                <th className="px-4 py-3">상태</th>
                                <th className="px-4 py-3">작업</th>
                            </tr>
                            </thead>
                            <tbody>
                            {products.map((product) => (
                                <tr key={product.id} className="border-t border-slate-100">
                                    <td className="px-4 py-3">{product.id}</td>
                                    <td className="px-4 py-3 font-medium">{product.name}</td>
                                    <td className="px-4 py-3">{formatMoney(product.price)}</td>
                                    <td className="px-4 py-3">{product.stock}</td>
                                    <td className="px-4 py-3">
                                        <StatusBadge status={product.status}/>
                                    </td>
                                    <td className="px-4 py-3">
                                        <div className="flex gap-2">
                                            <Link to={`/admin/products/${product.id}/edit`}>
                                                <Button variant="secondary">
                                                    수정
                                                </Button>
                                            </Link>
                                            <Button variant="danger" onClick={() => actions.deleteById(product.id)}>
                                                삭제
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </Card>
                )
            ) : null}
        </div>
    );
}

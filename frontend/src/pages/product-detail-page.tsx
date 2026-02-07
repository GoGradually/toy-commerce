import {useQuery} from '@tanstack/react-query';
import {Link, useParams} from 'react-router-dom';
import {useProductDetailActions} from '../features/products/hooks/use-product-detail-actions';
import {parsePositiveIntOrNull} from '../features/common/model/number-param';
import {toErrorMessage} from '../shared/api/core';
import {getProduct} from '../shared/api/products';
import {queryKeys} from '../shared/api/query-keys';
import {formatMoney} from '../shared/lib/format';
import {useMember} from '../shared/member/member-context';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {StatePanel} from '../shared/ui/state-panel';
import {StatusBadge} from '../shared/ui/status-badge';

export function ProductDetailPage() {
    const {memberId} = useMember();
    const {productId: rawProductId} = useParams();

    const productId = parsePositiveIntOrNull(rawProductId ?? '');
    const safeProductId = productId ?? 0;

    const productQuery = useQuery({
        queryKey: queryKeys.products.detail(safeProductId),
        queryFn: () => getProduct(safeProductId),
        enabled: productId !== null
    });

    const actions = useProductDetailActions(memberId, safeProductId);

    if (productId === null) {
        return <StatePanel title="잘못된 상품 ID입니다"/>;
    }

    if (productQuery.isLoading) {
        return <StatePanel title="상품을 불러오는 중..."/>;
    }

    if (productQuery.isError) {
        return <StatePanel title="상품을 찾을 수 없습니다" description={toErrorMessage(productQuery.error)}/>;
    }

    if (!productQuery.data) {
        return <StatePanel title="상품 데이터가 없습니다"/>;
    }

    const product = productQuery.data;

    return (
        <div className="space-y-4">
            <Link to="/">
                <Button variant="secondary">상품 목록으로</Button>
            </Link>
            <Card className="space-y-4">
                <div className="flex items-start justify-between gap-2">
                    <div>
                        <h2 className="text-2xl font-bold">{product.name}</h2>
                        <p className="text-sm text-slate-600">상품 ID: {product.id}</p>
                    </div>
                    <StatusBadge status={product.status}/>
                </div>
                <div className="grid gap-2 rounded-xl bg-slate-50 p-4 md:grid-cols-2">
                    <p className="text-sm text-slate-700">가격: {formatMoney(product.price)}</p>
                    <p className="text-sm text-slate-700">재고: {product.stock}</p>
                    <p className="text-sm text-slate-700">회원 헤더: {memberId}</p>
                </div>
                <div className="flex flex-wrap gap-2">
                    <Button variant="primary" onClick={actions.addToCart}>
                        장바구니 담기
                    </Button>
                    <Button variant="secondary" onClick={actions.addToWishlist}>
                        위시리스트 추가
                    </Button>
                    <Button variant="secondary" onClick={actions.removeWishlist}>
                        위시리스트 제거
                    </Button>
                    <Link to="/cart">
                        <Button variant="secondary">장바구니 열기</Button>
                    </Link>
                </div>

                {actions.errors.addToCart ? <p className="text-sm text-red-600">{actions.errors.addToCart}</p> : null}
                {actions.errors.addWishlist ?
                    <p className="text-sm text-red-600">{actions.errors.addWishlist}</p> : null}
                {actions.errors.removeWishlist ?
                    <p className="text-sm text-red-600">{actions.errors.removeWishlist}</p> : null}
            </Card>
        </div>
    );
}

import {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useCartActions} from '../features/cart/hooks/use-cart-actions';
import {useCartQuery} from '../features/cart/hooks/use-cart-query';
import {parseCartAddItemInput, parseCartQuantityInput} from '../features/cart/model/cart-input';
import {CartQuickAdd} from '../features/cart/ui/cart-quick-add';
import {toErrorMessage} from '../shared/api/core';
import {formatMoney} from '../shared/lib/format';
import {useMember} from '../shared/member/member-context';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {Input} from '../shared/ui/input';
import {StatePanel} from '../shared/ui/state-panel';

export function CartPage() {
    const {memberId} = useMember();
    const navigate = useNavigate();
    const [newProductId, setNewProductId] = useState('');
    const [newQuantity, setNewQuantity] = useState('1');

    const cartQuery = useCartQuery(memberId);
    const actions = useCartActions({
        memberId,
        onCheckoutSuccess: (orderId) => navigate(`/orders/${orderId}`)
    });

    if (cartQuery.isLoading) {
        return <StatePanel title="장바구니를 불러오는 중..."/>;
    }

    if (cartQuery.isError) {
        return <StatePanel title="장바구니를 불러오지 못했습니다" description={toErrorMessage(cartQuery.error)}/>;
    }

    if (!cartQuery.data) {
        return <StatePanel title="장바구니 데이터가 없습니다"/>;
    }

    const cart = cartQuery.data;

    return (
        <div className="space-y-4">
            <section
                className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <div>
                    <h2 className="text-xl font-bold">장바구니</h2>
                    <p className="text-sm text-slate-600">회원 헤더: {memberId}</p>
                </div>
                <div className="flex gap-2">
                    <Link to="/">
                        <Button variant="secondary">쇼핑 계속하기</Button>
                    </Link>
                    <Button variant="primary" onClick={actions.checkout}>
                        주문하기
                    </Button>
                    <Button variant="danger" onClick={actions.clearItems}>
                        장바구니 비우기
                    </Button>
                </div>
            </section>

            <CartQuickAdd
                productIdInput={newProductId}
                quantityInput={newQuantity}
                onProductIdInputChange={setNewProductId}
                onQuantityInputChange={setNewQuantity}
                onAddItem={() => {
                    const next = parseCartAddItemInput(newProductId, newQuantity);
                    if (next) {
                        actions.addItem(next);
                    }
                }}
            />

            {actions.mutationError ? <p className="text-sm font-medium text-red-700">{actions.mutationError}</p> : null}

            {cart.items.length === 0 ? (
                <StatePanel title="장바구니가 비어 있습니다" description="상품 목록에서 상품을 추가해 주세요."/>
            ) : (
                <div className="space-y-3">
                    {cart.items.map((item) => (
                        <Card key={item.productId}
                              className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                            <div>
                                <h3 className="text-lg font-semibold">{item.name}</h3>
                                <p className="text-sm text-slate-600">상품 #{item.productId}</p>
                                <p className="text-sm text-slate-600">
                                    단가 {formatMoney(item.price)} · 합계 {formatMoney(item.lineTotal)}
                                </p>
                            </div>
                            <div className="flex flex-wrap items-center gap-2">
                                <Input
                                    className="w-24"
                                    min={1}
                                    type="number"
                                    value={String(item.quantity)}
                                    onChange={(event) => {
                                        const quantity = parseCartQuantityInput(event.target.value);
                                        if (quantity !== null) {
                                            actions.updateItemQuantity({productId: item.productId, quantity});
                                        }
                                    }}
                                />
                                <Button variant="danger" onClick={() => actions.removeItem(item.productId)}>
                                    삭제
                                </Button>
                            </div>
                        </Card>
                    ))}
                </div>
            )}

            <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <p className="text-sm text-slate-600">장바구니 합계</p>
                <p className="text-2xl font-bold">{formatMoney(cart.cartTotal)}</p>
            </section>
        </div>
    );
}

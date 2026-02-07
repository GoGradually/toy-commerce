import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {useMemo, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {toErrorMessage} from '../shared/api/core';
import {getOrderDetail, payOrder} from '../shared/api/orders';
import {queryKeys} from '../shared/api/query-keys';
import {formatMoney} from '../shared/lib/format';
import {useMember} from '../shared/member/member-context';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {Input} from '../shared/ui/input';
import {StatePanel} from '../shared/ui/state-panel';
import {StatusBadge} from '../shared/ui/status-badge';

export function OrderDetailPage() {
    const {memberId} = useMember();
    const {orderId: rawOrderId} = useParams();
    const queryClient = useQueryClient();
    const [paymentToken, setPaymentToken] = useState('CARD_20260207_0001');

    const orderId = Number.parseInt(rawOrderId ?? '', 10);

    const orderQuery = useQuery({
        queryKey: queryKeys.orders.detail(memberId, orderId),
        queryFn: () => getOrderDetail(memberId, orderId),
        enabled: Number.isFinite(orderId) && orderId >= 1
    });

    const payMutation = useMutation({
        mutationFn: () => payOrder(memberId, orderId, {paymentToken}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.orders.detail(memberId, orderId)});
            queryClient.invalidateQueries({queryKey: queryKeys.cart.items(memberId)});
        }
    });

    const paymentHint = useMemo(
        () => [
            '토큰 접두사 FAIL_ -> 결제 실패',
            '토큰 접두사 TIMEOUT_ -> 타임아웃 시뮬레이션',
            '그 외 토큰 -> 성공'
        ],
        []
    );

    if (!Number.isFinite(orderId) || orderId < 1) {
        return <StatePanel title="잘못된 주문 ID입니다"/>;
    }

    if (orderQuery.isLoading) {
        return <StatePanel title="주문을 불러오는 중..."/>;
    }

    if (orderQuery.isError) {
        return <StatePanel title="주문을 불러오지 못했습니다" description={toErrorMessage(orderQuery.error)}/>;
    }

    if (!orderQuery.data) {
        return <StatePanel title="주문 데이터가 없습니다"/>;
    }

    const order = orderQuery.data;

    return (
        <div className="space-y-4">
            <section
                className="flex flex-wrap items-center justify-between gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-panel">
                <div>
                    <h2 className="text-xl font-bold">주문 #{order.orderId}</h2>
                    <p className="text-sm text-slate-600">
                        회원 {order.memberId} · 생성일 {new Date(order.createdAt).toLocaleString('ko-KR')}
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <StatusBadge status={order.status}/>
                    <Link to="/cart">
                        <Button variant="secondary">장바구니 열기</Button>
                    </Link>
                </div>
            </section>

            <Card className="space-y-3">
                <h3 className="text-lg font-semibold">주문 결제</h3>
                <div className="flex flex-wrap gap-2">
                    <Input
                        className="w-full md:w-96"
                        value={paymentToken}
                        onChange={(event) => setPaymentToken(event.target.value)}
                    />
                    <Button variant="primary" onClick={() => payMutation.mutate()}>
                        지금 결제
                    </Button>
                </div>
                <ul className="space-y-1 text-sm text-slate-600">
                    {paymentHint.map((line) => (
                        <li key={line}>{line}</li>
                    ))}
                </ul>
                {payMutation.isError ?
                    <p className="text-sm text-red-700">{toErrorMessage(payMutation.error)}</p> : null}
                {payMutation.isSuccess ? <p className="text-sm text-emerald-700">결제 요청이 처리되었습니다.</p> : null}
            </Card>

            <Card className="space-y-3">
                <h3 className="text-lg font-semibold">주문 상품</h3>
                {order.items.length === 0 ? (
                    <StatePanel title="주문 상품이 없습니다"/>
                ) : (
                    <div className="space-y-2">
                        {order.items.map((item) => (
                            <div key={`${item.productId}-${item.productName}`}
                                 className="rounded-xl border border-slate-200 p-3">
                                <p className="font-semibold">{item.productName}</p>
                                <p className="text-sm text-slate-600">
                                    상품 #{item.productId} · 수량 {item.quantity} · 단가 {formatMoney(item.unitPrice)}
                                </p>
                                <p className="text-sm font-semibold text-slate-900">합계 {formatMoney(item.lineTotal)}</p>
                            </div>
                        ))}
                    </div>
                )}
                <div className="border-t border-slate-200 pt-3">
                    <p className="text-sm text-slate-600">주문 총액</p>
                    <p className="text-2xl font-bold">{formatMoney(order.totalAmount)}</p>
                </div>
            </Card>
        </div>
    );
}

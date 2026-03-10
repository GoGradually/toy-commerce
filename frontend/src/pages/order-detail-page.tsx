import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import type {Dispatch, SetStateAction} from 'react';
import {useEffect, useMemo, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {completeOrderDetails, getOrderDetail, payOrder} from '../shared/api/orders';
import {toErrorMessage} from '../shared/api/core';
import {queryKeys} from '../shared/api/query-keys';
import type {CompleteOrderDetailsRequest, OrderDetailResponse, PaymentMethod} from '../shared/api/generated/schema';
import {formatMoney} from '../shared/lib/format';
import {useMember} from '../shared/member/member-context';
import {Button} from '../shared/ui/button';
import {Card} from '../shared/ui/card';
import {Input} from '../shared/ui/input';
import {StatePanel} from '../shared/ui/state-panel';
import {StatusBadge} from '../shared/ui/status-badge';

const defaultDetailsForm: CompleteOrderDetailsRequest = {
    receiverName: '',
    receiverPhone: '',
    zipCode: '',
    addressLine1: '',
    addressLine2: '',
    couponCode: '',
    paymentMethod: 'CARD'
};

export function OrderDetailPage() {
    const {memberId} = useMember();
    const {orderId: rawOrderId} = useParams();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [paymentToken, setPaymentToken] = useState('CARD_20260207_0001');
    const [detailsForm, setDetailsForm] = useState<CompleteOrderDetailsRequest>(defaultDetailsForm);

    const orderId = Number.parseInt(rawOrderId ?? '', 10);

    const orderQuery = useQuery({
        queryKey: queryKeys.orders.detail(memberId, orderId),
        queryFn: () => getOrderDetail(memberId, orderId),
        enabled: Number.isFinite(orderId) && orderId >= 1
    });

    const completeDetailsMutation = useMutation({
        mutationFn: () => completeOrderDetails(memberId, orderId, normalizeDetailsForm(detailsForm)),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.orders.detail(memberId, orderId)});
        }
    });

    const payMutation = useMutation({
        mutationFn: () => payOrder(memberId, orderId, {paymentToken}),
        onSuccess: (response) => {
            queryClient.invalidateQueries({queryKey: queryKeys.orders.detail(memberId, orderId)});
            queryClient.invalidateQueries({queryKey: queryKeys.cart.items(memberId)});
            if (response.replacementOrderId) {
                queryClient.invalidateQueries({queryKey: queryKeys.orders.detail(memberId, response.replacementOrderId)});
                navigate(`/orders/${response.replacementOrderId}`);
            }
        }
    });

    useEffect(() => {
        if (!orderQuery.data) {
            return;
        }

        setDetailsForm(toDetailsForm(orderQuery.data));
    }, [orderQuery.data]);

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

            {order.status === 'CREATED' ? (
                <Card className="space-y-3">
                    <h3 className="text-lg font-semibold">주문 정보 입력</h3>
                    <div className="grid gap-3 md:grid-cols-2">
                        <LabeledInput
                            label="수령인명"
                            value={detailsForm.receiverName}
                            onChange={(value) => updateDetailsForm(setDetailsForm, 'receiverName', value)}
                        />
                        <LabeledInput
                            label="연락처"
                            value={detailsForm.receiverPhone}
                            onChange={(value) => updateDetailsForm(setDetailsForm, 'receiverPhone', value)}
                        />
                        <LabeledInput
                            label="우편번호"
                            value={detailsForm.zipCode}
                            onChange={(value) => updateDetailsForm(setDetailsForm, 'zipCode', value)}
                        />
                        <LabeledInput
                            label="기본 주소"
                            value={detailsForm.addressLine1}
                            onChange={(value) => updateDetailsForm(setDetailsForm, 'addressLine1', value)}
                        />
                        <LabeledInput
                            label="상세 주소"
                            value={detailsForm.addressLine2 ?? ''}
                            onChange={(value) => updateDetailsForm(setDetailsForm, 'addressLine2', value)}
                        />
                        <LabeledInput
                            label="쿠폰 코드"
                            value={detailsForm.couponCode ?? ''}
                            onChange={(value) => updateDetailsForm(setDetailsForm, 'couponCode', value)}
                        />
                    </div>
                    <div className="space-y-2">
                        <p className="text-sm font-medium text-slate-700">결제 수단</p>
                        <div className="flex gap-2">
                            <Button
                                variant={detailsForm.paymentMethod === 'CARD' ? 'primary' : 'secondary'}
                                onClick={() => updateDetailsForm(setDetailsForm, 'paymentMethod', 'CARD')}>
                                카드
                            </Button>
                            <Button
                                variant={detailsForm.paymentMethod === 'BANK_TRANSFER' ? 'primary' : 'secondary'}
                                onClick={() => updateDetailsForm(setDetailsForm, 'paymentMethod', 'BANK_TRANSFER')}>
                                계좌이체
                            </Button>
                        </div>
                    </div>
                    <PriceSummary order={order}/>
                    <Button variant="primary" onClick={() => completeDetailsMutation.mutate()}>
                        정보 입력 완료
                    </Button>
                    {completeDetailsMutation.isError ?
                        <p className="text-sm text-red-700">{toErrorMessage(completeDetailsMutation.error)}</p> : null}
                </Card>
            ) : null}

            {order.status !== 'CREATED' ? (
                <Card className="space-y-3">
                    <h3 className="text-lg font-semibold">주문 정보</h3>
                    <div className="grid gap-3 md:grid-cols-2">
                        <ReadOnlyField label="수령인명" value={order.orderDetails.receiverName}/>
                        <ReadOnlyField label="연락처" value={order.orderDetails.receiverPhone}/>
                        <ReadOnlyField label="우편번호" value={order.orderDetails.zipCode}/>
                        <ReadOnlyField label="기본 주소" value={order.orderDetails.addressLine1}/>
                        <ReadOnlyField label="상세 주소" value={order.orderDetails.addressLine2}/>
                        <ReadOnlyField label="쿠폰" value={order.orderDetails.couponCode ?? '-'}/>
                        <ReadOnlyField label="결제 수단" value={formatPaymentMethod(order.orderDetails.paymentMethod)}/>
                    </div>
                    <PriceSummary order={order}/>
                </Card>
            ) : null}

            {order.status === 'INFO_COMPLETED' ? (
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
                    {payMutation.isSuccess && !payMutation.data?.replacementOrderId ?
                        <p className="text-sm text-emerald-700">결제 요청이 처리되었습니다.</p> : null}
                </Card>
            ) : null}

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
            </Card>
        </div>
    );
}

function PriceSummary({order}: { order: OrderDetailResponse }) {
    return (
        <div className="grid gap-1 rounded-xl bg-slate-50 p-3 text-sm text-slate-700">
            <p>원 주문 금액: {formatMoney(order.originalAmount)}</p>
            <p>할인 금액: {formatMoney(order.discountAmount)}</p>
            <p className="text-base font-semibold text-slate-900">최종 결제 금액: {formatMoney(order.totalAmount)}</p>
        </div>
    );
}

function LabeledInput({
                          label,
                          value,
                          onChange
                      }: {
    label: string;
    value: string;
    onChange: (value: string) => void;
}) {
    return (
        <label className="space-y-1 text-sm font-medium text-slate-700">
            <span>{label}</span>
            <Input value={value} onChange={(event) => onChange(event.target.value)}/>
        </label>
    );
}

function ReadOnlyField({label, value}: { label: string; value: string | null }) {
    return (
        <div className="space-y-1">
            <p className="text-sm font-medium text-slate-700">{label}</p>
            <p className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900">
                {value ?? '-'}
            </p>
        </div>
    );
}

function toDetailsForm(order: OrderDetailResponse): CompleteOrderDetailsRequest {
    return {
        receiverName: order.orderDetails.receiverName ?? '',
        receiverPhone: order.orderDetails.receiverPhone ?? '',
        zipCode: order.orderDetails.zipCode ?? '',
        addressLine1: order.orderDetails.addressLine1 ?? '',
        addressLine2: order.orderDetails.addressLine2 ?? '',
        couponCode: order.orderDetails.couponCode ?? '',
        paymentMethod: order.orderDetails.paymentMethod ?? 'CARD'
    };
}

function normalizeDetailsForm(form: CompleteOrderDetailsRequest): CompleteOrderDetailsRequest {
    return {
        ...form,
        addressLine2: form.addressLine2?.trim() || undefined,
        couponCode: form.couponCode?.trim() || undefined
    };
}

function updateDetailsForm<K extends keyof CompleteOrderDetailsRequest>(
    setForm: Dispatch<SetStateAction<CompleteOrderDetailsRequest>>,
    key: K,
    value: CompleteOrderDetailsRequest[K]
) {
    setForm((current) => ({...current, [key]: value}));
}

function formatPaymentMethod(paymentMethod: PaymentMethod | null): string {
    if (paymentMethod === 'CARD') {
        return '카드';
    }

    if (paymentMethod === 'BANK_TRANSFER') {
        return '계좌이체';
    }

    return '-';
}

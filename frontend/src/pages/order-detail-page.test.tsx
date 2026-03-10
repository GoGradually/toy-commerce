import {screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {beforeEach, describe, expect, it, vi} from 'vitest';
import {renderPage} from '../test/render-page';
import {completeOrderDetails, getOrderDetail, payOrder} from '../shared/api/orders';
import {OrderDetailPage} from './order-detail-page';

vi.mock('../shared/api/orders', () => ({
    getOrderDetail: vi.fn(),
    completeOrderDetails: vi.fn(),
    payOrder: vi.fn()
}));

describe('OrderDetailPage', () => {
    beforeEach(() => {
        vi.mocked(getOrderDetail).mockReset();
        vi.mocked(completeOrderDetails).mockReset();
        vi.mocked(payOrder).mockReset();
    });

    it('submits order details when order is created', async () => {
        const user = userEvent.setup();
        vi.mocked(getOrderDetail).mockResolvedValue({
            orderId: 1,
            memberId: 1,
            status: 'CREATED',
            originalAmount: 31800,
            discountAmount: 0,
            totalAmount: 31800,
            items: [],
            orderDetails: {
                receiverName: null,
                receiverPhone: null,
                zipCode: null,
                addressLine1: null,
                addressLine2: null,
                couponCode: null,
                paymentMethod: null
            },
            createdAt: '2026-02-07T10:15:30'
        });
        vi.mocked(completeOrderDetails).mockResolvedValue({
            orderId: 1,
            status: 'INFO_COMPLETED',
            originalAmount: 31800,
            discountAmount: 3180,
            totalAmount: 28620,
            couponCode: 'WELCOME10',
            paymentMethod: 'CARD'
        });

        renderPage({
            path: '/orders/:orderId',
            element: <OrderDetailPage/>,
            initialEntry: '/orders/1'
        });

        await screen.findByText('주문 정보 입력');
        await user.type(screen.getByLabelText('수령인명'), '홍길동');
        await user.type(screen.getByLabelText('연락처'), '01012345678');
        await user.type(screen.getByLabelText('우편번호'), '06236');
        await user.type(screen.getByLabelText('기본 주소'), '서울특별시 강남구 테헤란로 123');
        await user.type(screen.getByLabelText('상세 주소'), '101동 202호');
        await user.type(screen.getByLabelText('쿠폰 코드'), 'WELCOME10');
        await user.click(screen.getByRole('button', {name: '정보 입력 완료'}));

        await waitFor(() => {
            expect(completeOrderDetails).toHaveBeenCalledWith(1, 1, expect.objectContaining({
                receiverName: '홍길동',
                couponCode: 'WELCOME10',
                paymentMethod: 'CARD'
            }));
        });
    });

    it('submits payment when order is info completed', async () => {
        const user = userEvent.setup();
        vi.mocked(getOrderDetail).mockResolvedValue({
            orderId: 1,
            memberId: 1,
            status: 'INFO_COMPLETED',
            originalAmount: 31800,
            discountAmount: 3180,
            totalAmount: 28620,
            items: [],
            orderDetails: {
                receiverName: '홍길동',
                receiverPhone: '01012345678',
                zipCode: '06236',
                addressLine1: '서울특별시 강남구 테헤란로 123',
                addressLine2: '101동 202호',
                couponCode: 'WELCOME10',
                paymentMethod: 'CARD'
            },
            createdAt: '2026-02-07T10:15:30'
        });
        vi.mocked(payOrder).mockResolvedValue({
            orderId: 1,
            status: 'PAID',
            paid: true,
            paymentResult: 'SUCCESS'
        });

        renderPage({
            path: '/orders/:orderId',
            element: <OrderDetailPage/>,
            initialEntry: '/orders/1'
        });

        await screen.findByText('주문 결제');
        await user.click(screen.getByRole('button', {name: '지금 결제'}));

        await waitFor(() => {
            expect(payOrder).toHaveBeenCalledWith(1, 1, {paymentToken: 'CARD_20260207_0001'});
        });
    });
});

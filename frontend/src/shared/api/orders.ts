import {OrdersService} from './generated/client';
import {normalizeClientError, unwrapApiEnvelope} from './core';
import type {CheckoutOrderResponse, OrderDetailResponse, PayOrderRequest, PayOrderResponse} from './generated/schema';

export async function checkoutOrder(memberId: number): Promise<CheckoutOrderResponse> {
    try {
        const response = await OrdersService.checkout({xMemberId: memberId});
        return unwrapApiEnvelope(response) as CheckoutOrderResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function payOrder(
    memberId: number,
    orderId: number,
    request: PayOrderRequest
): Promise<PayOrderResponse> {
    try {
        const response = await OrdersService.pay({
            xMemberId: memberId,
            orderId,
            requestBody: request
        });
        return unwrapApiEnvelope(response) as PayOrderResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function getOrderDetail(memberId: number, orderId: number): Promise<OrderDetailResponse> {
    try {
        const response = await OrdersService.getOrder({
            xMemberId: memberId,
            orderId
        });
        return unwrapApiEnvelope(response) as OrderDetailResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

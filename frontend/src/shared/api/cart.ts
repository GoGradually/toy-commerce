import {CartService as GeneratedCartService} from './generated/client';
import {normalizeClientError, unwrapApiEnvelope} from './core';
import type {AddCartItemRequest, CartResponse, UpdateCartItemQuantityRequest} from './generated/schema';

export async function getCart(memberId: number): Promise<CartResponse> {
    try {
        const response = await GeneratedCartService.getCartItems({xMemberId: memberId});
        return unwrapApiEnvelope(response) as CartResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function addCartItem(memberId: number, request: AddCartItemRequest): Promise<void> {
    try {
        const response = await GeneratedCartService.addCartItem({
            xMemberId: memberId,
            requestBody: request
        });
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function updateCartItemQuantity(
    memberId: number,
    productId: number,
    request: UpdateCartItemQuantityRequest
): Promise<void> {
    try {
        const response = await GeneratedCartService.updateCartItemQuantity({
            xMemberId: memberId,
            productId,
            requestBody: request
        });
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function removeCartItem(memberId: number, productId: number): Promise<void> {
    try {
        const response = await GeneratedCartService.removeCartItem({
            xMemberId: memberId,
            productId
        });
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function clearCart(memberId: number): Promise<void> {
    try {
        const response = await GeneratedCartService.clearCart({xMemberId: memberId});
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

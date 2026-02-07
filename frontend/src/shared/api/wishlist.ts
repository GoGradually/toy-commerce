import {WishlistRankingsService, WishlistService} from './generated/client';
import {normalizeClientError, unwrapApiEnvelope} from './core';
import type {WishlistPopularRankingResponse} from './generated/schema';

export async function addWishlist(productId: number, memberId: number): Promise<void> {
    try {
        const response = await WishlistService.addWishlist({
            xMemberId: memberId,
            productId
        });
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function removeWishlist(productId: number, memberId: number): Promise<void> {
    try {
        const response = await WishlistService.removeWishlist({
            xMemberId: memberId,
            productId
        });
        unwrapApiEnvelope(response, {allowNullData: true});
    } catch (error) {
        throw normalizeClientError(error);
    }
}

export async function getWishlistRanking(limit: number): Promise<WishlistPopularRankingResponse> {
    try {
        const response = await WishlistRankingsService.getPopularWishlistRankings({limit});
        return unwrapApiEnvelope(response) as WishlistPopularRankingResponse;
    } catch (error) {
        throw normalizeClientError(error);
    }
}

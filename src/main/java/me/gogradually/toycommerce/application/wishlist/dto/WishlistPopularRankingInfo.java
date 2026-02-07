package me.gogradually.toycommerce.application.wishlist.dto;

import java.util.List;

public record WishlistPopularRankingInfo(
        List<WishlistPopularRankingItemInfo> rankings
) {
}

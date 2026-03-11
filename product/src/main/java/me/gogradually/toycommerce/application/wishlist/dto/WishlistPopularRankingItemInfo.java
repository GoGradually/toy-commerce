package me.gogradually.toycommerce.application.wishlist.dto;

import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.math.BigDecimal;

public record WishlistPopularRankingItemInfo(
        int rank,
        Long productId,
        String name,
        BigDecimal price,
        ProductStatus status,
        long wishlistCount
) {

    public static WishlistPopularRankingItemInfo from(int rank, Product product, long wishlistCount) {
        return new WishlistPopularRankingItemInfo(
                rank,
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStatus(),
                wishlistCount
        );
    }
}

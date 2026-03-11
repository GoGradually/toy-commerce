package me.gogradually.toycommerce.domain.wishlist;

import java.util.List;

public interface WishlistRankingRepository {

    void increaseScore(Long productId);

    void decreaseScore(Long productId);

    List<WishlistRankingScore> findTopRanked(int limit);
}

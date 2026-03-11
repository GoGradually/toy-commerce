package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.domain.wishlist.WishlistRankingRepository;
import me.gogradually.toycommerce.domain.wishlist.WishlistRankingScore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisSortedSetWishlistRankingRepository implements WishlistRankingRepository {

    private static final String POPULAR_WISHLIST_KEY = "ranking:wishlist:popular";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void increaseScore(Long productId) {
        redisTemplate.opsForZSet().incrementScore(POPULAR_WISHLIST_KEY, String.valueOf(productId), 1D);
    }

    @Override
    public void decreaseScore(Long productId) {
        Double score = redisTemplate.opsForZSet().incrementScore(POPULAR_WISHLIST_KEY, String.valueOf(productId), -1D);
        if (score != null && score <= 0D) {
            redisTemplate.opsForZSet().remove(POPULAR_WISHLIST_KEY, String.valueOf(productId));
        }
    }

    @Override
    public List<WishlistRankingScore> findTopRanked(int limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(POPULAR_WISHLIST_KEY, 0, limit - 1L);
        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(this::toRankingScore)
                .filter(score -> score != null)
                .toList();
    }

    private WishlistRankingScore toRankingScore(ZSetOperations.TypedTuple<String> tuple) {
        if (tuple.getValue() == null || tuple.getScore() == null) {
            return null;
        }

        try {
            return new WishlistRankingScore(
                    Long.parseLong(tuple.getValue()),
                    tuple.getScore().longValue()
            );
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}

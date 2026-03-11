package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import me.gogradually.toycommerce.domain.wishlist.WishlistRankingScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisSortedSetWishlistRankingRepositoryTest {

    private static final String POPULAR_WISHLIST_KEY = "ranking:wishlist:popular";

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RedisSortedSetWishlistRankingRepository repository;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void shouldIncreaseScore() {
        repository.increaseScore(11L);

        verify(zSetOperations).incrementScore(POPULAR_WISHLIST_KEY, "11", 1D);
    }

    @Test
    void shouldRemoveProductWhenScoreBecomesZeroOrLess() {
        when(zSetOperations.incrementScore(POPULAR_WISHLIST_KEY, "11", -1D)).thenReturn(0D);

        repository.decreaseScore(11L);

        verify(zSetOperations).remove(POPULAR_WISHLIST_KEY, "11");
    }

    @Test
    void shouldNotRemoveProductWhenScoreIsStillPositive() {
        when(zSetOperations.incrementScore(POPULAR_WISHLIST_KEY, "11", -1D)).thenReturn(2D);

        repository.decreaseScore(11L);

        verify(zSetOperations, never()).remove(POPULAR_WISHLIST_KEY, "11");
    }

    @Test
    void shouldReturnEmptyListWhenRankingIsMissing() {
        when(zSetOperations.reverseRangeWithScores(POPULAR_WISHLIST_KEY, 0, 9)).thenReturn(null);

        List<WishlistRankingScore> result = repository.findTopRanked(10);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapValidTupleAndFilterInvalidTuples() {
        Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
        tuples.add(new DefaultTypedTuple<>("101", 5D));
        tuples.add(new DefaultTypedTuple<>("invalid", 4D));
        tuples.add(new DefaultTypedTuple<>(null, 3D));
        tuples.add(new DefaultTypedTuple<>("202", null));

        when(zSetOperations.reverseRangeWithScores(POPULAR_WISHLIST_KEY, 0, 9)).thenReturn(tuples);

        List<WishlistRankingScore> result = repository.findTopRanked(10);

        assertThat(result).containsExactly(new WishlistRankingScore(101L, 5L));
    }
}


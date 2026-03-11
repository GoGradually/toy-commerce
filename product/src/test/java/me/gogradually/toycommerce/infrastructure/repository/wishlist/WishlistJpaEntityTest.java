package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import me.gogradually.toycommerce.domain.wishlist.Wishlist;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistJpaEntityTest {

    @Test
    void shouldMapDomainToEntityAndBack() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 7, 10, 15, 30);
        Wishlist wishlist = Wishlist.restore(1L, 1001L, 11L, createdAt);

        WishlistJpaEntity entity = WishlistJpaEntity.from(wishlist);
        Wishlist restored = entity.toDomain();

        assertThat(restored.getId()).isEqualTo(1L);
        assertThat(restored.getMemberId()).isEqualTo(1001L);
        assertThat(restored.getProductId()).isEqualTo(11L);
        assertThat(restored.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldPopulateCreatedAtWhenPrePersistRunsOnNewEntity() {
        WishlistJpaEntity entity = WishlistJpaEntity.from(Wishlist.create(1001L, 11L));

        entity.prePersist();

        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldKeepExistingCreatedAtWhenPrePersistRuns() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 7, 10, 15, 30);
        WishlistJpaEntity entity = WishlistJpaEntity.from(Wishlist.restore(1L, 1001L, 11L, createdAt));

        entity.prePersist();

        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }
}

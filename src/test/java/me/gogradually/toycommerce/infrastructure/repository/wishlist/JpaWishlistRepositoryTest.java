package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import me.gogradually.toycommerce.domain.wishlist.Wishlist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaWishlistRepositoryTest {

    @Mock
    private SpringDataWishlistJpaRepository jpaRepository;

    @InjectMocks
    private JpaWishlistRepository wishlistRepository;

    @Test
    void shouldDelegateExistsByMemberIdAndProductId() {
        when(jpaRepository.existsByMemberIdAndProductId(1001L, 11L)).thenReturn(true);

        boolean result = wishlistRepository.existsByMemberIdAndProductId(1001L, 11L);

        assertThat(result).isTrue();
    }

    @Test
    void shouldSaveWishlistWithEntityMapping() {
        Wishlist wishlist = Wishlist.restore(1L, 1001L, 11L, LocalDateTime.of(2026, 2, 7, 10, 0));
        when(jpaRepository.save(any(WishlistJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wishlist saved = wishlistRepository.save(wishlist);

        verify(jpaRepository).save(any(WishlistJpaEntity.class));
        assertThat(saved.getMemberId()).isEqualTo(1001L);
        assertThat(saved.getProductId()).isEqualTo(11L);
    }

    @Test
    void shouldReturnTrueWhenDeleteAffectedAnyRow() {
        when(jpaRepository.deleteByMemberIdAndProductId(1001L, 11L)).thenReturn(1L);

        boolean deleted = wishlistRepository.deleteByMemberIdAndProductId(1001L, 11L);

        assertThat(deleted).isTrue();
    }

    @Test
    void shouldReturnFalseWhenDeleteAffectedNoRow() {
        when(jpaRepository.deleteByMemberIdAndProductId(1001L, 11L)).thenReturn(0L);

        boolean deleted = wishlistRepository.deleteByMemberIdAndProductId(1001L, 11L);

        assertThat(deleted).isFalse();
    }
}

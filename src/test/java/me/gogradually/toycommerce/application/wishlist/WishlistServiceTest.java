package me.gogradually.toycommerce.application.wishlist;

import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingInfo;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import me.gogradually.toycommerce.domain.wishlist.Wishlist;
import me.gogradually.toycommerce.domain.wishlist.WishlistRankingRepository;
import me.gogradually.toycommerce.domain.wishlist.WishlistRankingScore;
import me.gogradually.toycommerce.domain.wishlist.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistRankingRepository wishlistRankingRepository;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void shouldAddWishlist() {
        Product activeProduct = activeProduct(100L);

        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE))
                .thenReturn(Optional.of(activeProduct));
        when(wishlistRepository.existsByMemberIdAndProductId(1L, 100L)).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        wishlistService.addWishlist(1L, 100L);

        verify(wishlistRepository).save(any(Wishlist.class));
        verify(wishlistRankingRepository).increaseScore(100L);
    }

    @Test
    void shouldBeIdempotentWhenAddingAlreadyWishlistedProduct() {
        Product activeProduct = activeProduct(100L);

        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE))
                .thenReturn(Optional.of(activeProduct));
        when(wishlistRepository.existsByMemberIdAndProductId(1L, 100L)).thenReturn(true);

        wishlistService.addWishlist(1L, 100L);

        verify(wishlistRepository, never()).save(any(Wishlist.class));
        verify(wishlistRankingRepository, never()).increaseScore(any());
    }

    @Test
    void shouldBeIdempotentWhenDuplicateInsertOccurs() {
        Product activeProduct = activeProduct(100L);

        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE))
                .thenReturn(Optional.of(activeProduct));
        when(wishlistRepository.existsByMemberIdAndProductId(1L, 100L)).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        wishlistService.addWishlist(1L, 100L);

        verify(wishlistRankingRepository, never()).increaseScore(any());
    }

    @Test
    void shouldBeIdempotentWhenRemovingMissingWishlist() {
        when(wishlistRepository.deleteByMemberIdAndProductId(1L, 100L)).thenReturn(false);

        wishlistService.removeWishlist(1L, 100L);

        verify(wishlistRankingRepository, never()).decreaseScore(any());
    }

    @Test
    void shouldDecreaseRankingWhenRemovingExistingWishlist() {
        when(wishlistRepository.deleteByMemberIdAndProductId(1L, 100L)).thenReturn(true);

        wishlistService.removeWishlist(1L, 100L);

        verify(wishlistRankingRepository).decreaseScore(100L);
    }

    @Test
    void shouldGetPopularRankings() {
        Product topProduct = activeProduct(100L);
        when(wishlistRankingRepository.findTopRanked(10))
                .thenReturn(List.of(
                        new WishlistRankingScore(100L, 12L),
                        new WishlistRankingScore(200L, 8L)
                ));
        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.of(topProduct));
        when(productRepository.findByIdAndStatus(200L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());

        WishlistPopularRankingInfo result = wishlistService.getPopularRankings(10);

        assertThat(result.rankings()).hasSize(1);
        assertThat(result.rankings().getFirst().rank()).isEqualTo(1);
        assertThat(result.rankings().getFirst().productId()).isEqualTo(100L);
        assertThat(result.rankings().getFirst().wishlistCount()).isEqualTo(12L);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findByIdAndStatus(999L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistService.addWishlist(1L, 999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWhenProductIsInactive() {
        Product inactive = inactiveProduct(10L);
        when(productRepository.findByIdAndStatus(10L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());
        when(productRepository.findById(10L)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> wishlistService.addWishlist(1L, 10L))
                .isInstanceOf(InactiveProductException.class);
    }

    private Product activeProduct(Long id) {
        return Product.restore(
                id,
                "레고 인기 상품",
                new BigDecimal("25900"),
                20,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1)
        );
    }

    private Product inactiveProduct(Long id) {
        return Product.restore(
                id,
                "품절 상품",
                new BigDecimal("15900"),
                0,
                ProductStatus.INACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2)
        );
    }
}

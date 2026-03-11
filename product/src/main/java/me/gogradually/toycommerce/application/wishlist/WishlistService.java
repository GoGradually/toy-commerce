package me.gogradually.toycommerce.application.wishlist;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingInfo;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingItemInfo;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import me.gogradually.toycommerce.domain.wishlist.Wishlist;
import me.gogradually.toycommerce.domain.wishlist.WishlistRankingRepository;
import me.gogradually.toycommerce.domain.wishlist.WishlistRankingScore;
import me.gogradually.toycommerce.domain.wishlist.WishlistRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistRankingRepository wishlistRankingRepository;

    @Transactional
    public void addWishlist(Long memberId, Long productId) {
        ensureProductCanBeWishlisted(productId);

        if (wishlistRepository.existsByMemberIdAndProductId(memberId, productId)) {
            return;
        }

        try {
            wishlistRepository.save(Wishlist.create(memberId, productId));
        } catch (DataIntegrityViolationException exception) {
            return;
        }

        wishlistRankingRepository.increaseScore(productId);
    }

    @Transactional
    public void removeWishlist(Long memberId, Long productId) {
        boolean deleted = wishlistRepository.deleteByMemberIdAndProductId(memberId, productId);
        if (!deleted) {
            return;
        }

        wishlistRankingRepository.decreaseScore(productId);
    }

    public WishlistPopularRankingInfo getPopularRankings(int limit) {
        List<WishlistRankingScore> rankedProducts = wishlistRankingRepository.findTopRanked(limit);
        List<WishlistPopularRankingItemInfo> rankings = new ArrayList<>();

        int rank = 1;
        for (WishlistRankingScore rankedProduct : rankedProducts) {
            Product product = productRepository.findByIdAndStatus(rankedProduct.productId(), ProductStatus.ACTIVE)
                    .orElse(null);
            if (product == null) {
                continue;
            }

            rankings.add(WishlistPopularRankingItemInfo.from(rank, product, rankedProduct.score()));
            rank++;
        }

        return new WishlistPopularRankingInfo(rankings);
    }

    private void ensureProductCanBeWishlisted(Long productId) {
        if (productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE).isPresent()) {
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        throw new InactiveProductException(product.getId(), product.getStatus());
    }
}

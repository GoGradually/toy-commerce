package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.domain.wishlist.Wishlist;
import me.gogradually.toycommerce.domain.wishlist.WishlistRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaWishlistRepository implements WishlistRepository {

    private final SpringDataWishlistJpaRepository jpaRepository;

    @Override
    public boolean existsByMemberIdAndProductId(Long memberId, Long productId) {
        return jpaRepository.existsByMemberIdAndProductId(memberId, productId);
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        WishlistJpaEntity saved = jpaRepository.save(WishlistJpaEntity.from(wishlist));
        return saved.toDomain();
    }

    @Override
    public boolean deleteByMemberIdAndProductId(Long memberId, Long productId) {
        return jpaRepository.deleteByMemberIdAndProductId(memberId, productId) > 0;
    }
}

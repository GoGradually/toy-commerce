package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWishlistJpaRepository extends JpaRepository<WishlistJpaEntity, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    long deleteByMemberIdAndProductId(Long memberId, Long productId);
}

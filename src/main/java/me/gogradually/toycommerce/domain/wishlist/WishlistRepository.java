package me.gogradually.toycommerce.domain.wishlist;

public interface WishlistRepository {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    Wishlist save(Wishlist wishlist);

    boolean deleteByMemberIdAndProductId(Long memberId, Long productId);
}

package me.gogradually.toycommerce.domain.cart;

import java.util.List;
import java.util.Optional;

public interface CartRepository {

    List<CartItem> findByMemberId(Long memberId);

    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);

    CartItem save(CartItem cartItem);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberId(Long memberId);
}

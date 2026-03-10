package me.gogradually.toycommerce.infrastructure.repository.cart;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCartRepository implements CartRepository {

    private final SpringDataCartItemJpaRepository jpaRepository;

    @Override
    public List<CartItem> findByMemberId(Long memberId) {
        return jpaRepository.findByMemberIdOrderByCreatedAtAsc(memberId).stream()
                .map(CartItemJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId) {
        return jpaRepository.findByMemberIdAndProductId(memberId, productId)
                .map(CartItemJpaEntity::toDomain);
    }

    @Override
    public CartItem save(CartItem cartItem) {
        CartItemJpaEntity saved = jpaRepository.save(CartItemJpaEntity.from(cartItem));
        return saved.toDomain();
    }

    @Override
    public void deleteByMemberIdAndProductId(Long memberId, Long productId) {
        jpaRepository.deleteByMemberIdAndProductId(memberId, productId);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        jpaRepository.deleteByMemberId(memberId);
    }
}

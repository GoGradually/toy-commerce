package me.gogradually.toycommerce.infrastructure.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCartItemJpaRepository extends JpaRepository<CartItemJpaEntity, Long> {

    List<CartItemJpaEntity> findByMemberIdOrderByCreatedAtAsc(Long memberId);

    Optional<CartItemJpaEntity> findByMemberIdAndProductId(Long memberId, Long productId);

    long deleteByMemberIdAndProductId(Long memberId, Long productId);

    long deleteByMemberId(Long memberId);
}

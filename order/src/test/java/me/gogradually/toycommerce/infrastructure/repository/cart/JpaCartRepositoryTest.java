package me.gogradually.toycommerce.infrastructure.repository.cart;

import me.gogradually.toycommerce.domain.cart.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCartRepositoryTest {

    @Mock
    private SpringDataCartItemJpaRepository jpaRepository;

    @InjectMocks
    private JpaCartRepository cartRepository;

    @Test
    void shouldMapCartItemsFromJpaEntity() {
        CartItem cartItem = restoreCartItem(1L, 1001L, 11L, 2);
        CartItemJpaEntity entity = CartItemJpaEntity.from(cartItem);
        when(jpaRepository.findByMemberIdOrderByCreatedAtAsc(1001L)).thenReturn(List.of(entity));

        List<CartItem> result = cartRepository.findByMemberId(1001L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMemberId()).isEqualTo(1001L);
        assertThat(result.getFirst().getProductId()).isEqualTo(11L);
        assertThat(result.getFirst().getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldMapOptionalCartItemWhenFound() {
        CartItem cartItem = restoreCartItem(1L, 1001L, 11L, 2);
        when(jpaRepository.findByMemberIdAndProductId(1001L, 11L))
                .thenReturn(Optional.of(CartItemJpaEntity.from(cartItem)));

        Optional<CartItem> result = cartRepository.findByMemberIdAndProductId(1001L, 11L);

        assertThat(result).isPresent();
        assertThat(result.get().getProductId()).isEqualTo(11L);
    }

    @Test
    void shouldReturnEmptyWhenCartItemIsMissing() {
        when(jpaRepository.findByMemberIdAndProductId(1001L, 99L)).thenReturn(Optional.empty());

        Optional<CartItem> result = cartRepository.findByMemberIdAndProductId(1001L, 99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveCartItemWithDomainMapping() {
        CartItem cartItem = restoreCartItem(null, 1001L, 11L, 3);
        when(jpaRepository.save(any(CartItemJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItem saved = cartRepository.save(cartItem);

        ArgumentCaptor<CartItemJpaEntity> captor = ArgumentCaptor.forClass(CartItemJpaEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertThat(captor.getValue().getMemberId()).isEqualTo(1001L);
        assertThat(captor.getValue().getProductId()).isEqualTo(11L);
        assertThat(saved.getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldDelegateDeleteByMemberIdAndProductId() {
        cartRepository.deleteByMemberIdAndProductId(1001L, 11L);

        verify(jpaRepository).deleteByMemberIdAndProductId(1001L, 11L);
    }

    @Test
    void shouldDelegateDeleteByMemberId() {
        cartRepository.deleteByMemberId(1001L);

        verify(jpaRepository).deleteByMemberId(1001L);
    }

    private CartItem restoreCartItem(Long id, Long memberId, Long productId, int quantity) {
        LocalDateTime now = LocalDateTime.of(2026, 2, 7, 18, 0, 0);
        return CartItem.restore(id, memberId, productId, quantity, now, now);
    }
}


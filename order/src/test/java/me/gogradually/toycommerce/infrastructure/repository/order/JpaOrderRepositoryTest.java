package me.gogradually.toycommerce.infrastructure.repository.order;

import me.gogradually.toycommerce.domain.order.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaOrderRepositoryTest {

    @Mock
    private SpringDataOrderJpaRepository jpaRepository;

    @InjectMocks
    private JpaOrderRepository orderRepository;

    @Test
    void shouldMapExpiredCancellationTargetsFromJpaEntities() {
        LocalDateTime cutoff = LocalDateTime.of(2026, 3, 10, 8, 30, 0);
        OrderJpaEntity entity = OrderJpaEntity.from(restoreOrder(1L, OrderStatus.INFO_COMPLETED));
        when(jpaRepository.findExpiredCancellationTargets(List.of(OrderStatus.CREATED, OrderStatus.INFO_COMPLETED), cutoff))
                .thenReturn(List.of(entity));

        List<ExpiredOrderCancellationTarget> result =
                orderRepository.findExpiredCancellationTargets(List.of(OrderStatus.CREATED, OrderStatus.INFO_COMPLETED), cutoff);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().orderId()).isEqualTo(1L);
        assertThat(result.getFirst().items())
                .extracting(OrderItem::getProductId)
                .containsExactly(11L);
    }

    @Test
    void shouldFindLatestOpenOrder() {
        OrderJpaEntity entity = OrderJpaEntity.from(restoreOrder(1L, OrderStatus.INFO_COMPLETED));
        when(jpaRepository.findFirstByMemberIdAndStatusInOrderByUpdatedAtDescIdDesc(
                1001L,
                List.of(OrderStatus.CREATED, OrderStatus.INFO_COMPLETED)
        )).thenReturn(Optional.of(entity));

        Optional<Order> result = orderRepository.findLatestOpenOrder(1001L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(jpaRepository).findFirstByMemberIdAndStatusInOrderByUpdatedAtDescIdDesc(
                1001L,
                List.of(OrderStatus.CREATED, OrderStatus.INFO_COMPLETED)
        );
    }

    @Test
    void shouldDelegateBulkCancelWithCancelledStatus() {
        LocalDateTime updatedAt = LocalDateTime.of(2026, 3, 10, 9, 0, 0);
        when(jpaRepository.cancelExpiredOrders(List.of(1L, 2L), OrderStatus.CANCELLED, updatedAt)).thenReturn(2);

        int cancelledCount = orderRepository.cancelExpiredOrders(List.of(1L, 2L), updatedAt);

        assertThat(cancelledCount).isEqualTo(2);
        verify(jpaRepository).cancelExpiredOrders(List.of(1L, 2L), OrderStatus.CANCELLED, updatedAt);
    }

    @Test
    void shouldNotCallBulkCancelWhenIdsAreEmpty() {
        LocalDateTime updatedAt = LocalDateTime.of(2026, 3, 10, 9, 0, 0);

        int cancelledCount = orderRepository.cancelExpiredOrders(List.of(), updatedAt);

        assertThat(cancelledCount).isZero();
        verify(jpaRepository, never()).cancelExpiredOrders(List.of(), OrderStatus.CANCELLED, updatedAt);
    }

    private Order restoreOrder(Long orderId, OrderStatus status) {
        LocalDateTime now = LocalDateTime.of(2026, 3, 9, 8, 0, 0);
        OrderItem item = OrderItem.restore(
                1L,
                orderId,
                11L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                1,
                new BigDecimal("15900"),
                now,
                now
        );
        OrderDetails details = status == OrderStatus.INFO_COMPLETED
                ? OrderDetails.complete("홍길동", "01012345678", "06236", "서울특별시 강남구 테헤란로 123", null, null, PaymentMethod.CARD)
                : OrderDetails.empty();

        return Order.restore(
                orderId,
                1001L,
                status,
                details,
                new BigDecimal("15900"),
                BigDecimal.ZERO,
                new BigDecimal("15900"),
                List.of(item),
                now,
                now
        );
    }
}

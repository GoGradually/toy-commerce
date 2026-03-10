package me.gogradually.toycommerce.infrastructure.repository.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.domain.order.ExpiredOrderCancellationTarget;
import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderRepository;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

    private static final List<OrderStatus> OPEN_STATUSES = List.of(
            OrderStatus.CREATED,
            OrderStatus.INFO_COMPLETED
    );

    private final SpringDataOrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(OrderJpaEntity.from(order));
        return saved.toDomain();
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return jpaRepository.findByIdWithItems(orderId)
                .map(OrderJpaEntity::toDomain);
    }

    @Override
    public Optional<Order> findByIdForUpdate(Long orderId) {
        return jpaRepository.findByIdWithItemsForUpdate(orderId)
                .map(OrderJpaEntity::toDomain);
    }

    @Override
    public Optional<Order> findLatestOpenOrder(Long memberId) {
        return jpaRepository.findFirstByMemberIdAndStatusInOrderByUpdatedAtDescIdDesc(memberId, OPEN_STATUSES)
                .map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<ExpiredOrderCancellationTarget> findExpiredCancellationTargets(List<OrderStatus> statuses, LocalDateTime createdAt) {
        return jpaRepository.findExpiredCancellationTargets(statuses, createdAt).stream()
                .map(OrderJpaEntity::toExpiredCancellationTarget)
                .toList();
    }

    @Override
    public int cancelExpiredOrders(List<Long> orderIds, LocalDateTime updatedAt) {
        if (orderIds.isEmpty()) {
            return 0;
        }

        return jpaRepository.cancelExpiredOrders(orderIds, OrderStatus.CANCELLED, updatedAt);
    }
}

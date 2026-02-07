package me.gogradually.toycommerce.infrastructure.repository.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

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
}

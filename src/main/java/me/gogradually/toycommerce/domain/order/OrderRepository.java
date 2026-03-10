package me.gogradually.toycommerce.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long orderId);

    Optional<Order> findByIdForUpdate(Long orderId);

    List<ExpiredOrderCancellationTarget> findExpiredCancellationTargets(List<OrderStatus> statuses, LocalDateTime createdAt);

    int cancelExpiredOrders(List<Long> orderIds, LocalDateTime updatedAt);
}

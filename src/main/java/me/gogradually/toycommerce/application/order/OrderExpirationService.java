package me.gogradually.toycommerce.application.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.domain.order.ExpiredOrderCancellationTarget;
import me.gogradually.toycommerce.domain.order.OrderRepository;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderExpirationService {

    private static final List<OrderStatus> EXPIRABLE_STATUSES = List.of(
            OrderStatus.CREATED,
            OrderStatus.INFO_COMPLETED
    );

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Clock clock;

    @Value("${app.order.expiration-minutes:30}")
    private long expirationMinutes;

    @Transactional
    public int cancelExpiredOrders() {
        LocalDateTime updatedAt = LocalDateTime.now(clock);
        LocalDateTime cutoff = updatedAt.minusMinutes(expirationMinutes);
        List<ExpiredOrderCancellationTarget> expiredOrders =
                orderRepository.findExpiredCancellationTargets(EXPIRABLE_STATUSES, cutoff);
        if (expiredOrders.isEmpty()) {
            return 0;
        }

        List<Long> orderIds = expiredOrders.stream()
                .map(ExpiredOrderCancellationTarget::orderId)
                .toList();
        int cancelledCount = orderRepository.cancelExpiredOrders(orderIds, updatedAt);
        if (cancelledCount != expiredOrders.size()) {
            throw new IllegalStateException("Expired order cancellation count mismatch.");
        }

        for (ExpiredOrderCancellationTarget expiredOrder : expiredOrders) {
            applicationEventPublisher.publishEvent(OrderCancelledEvent.from(expiredOrder));
        }
        return cancelledCount;
    }
}

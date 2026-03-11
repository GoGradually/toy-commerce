package me.gogradually.toycommerce.application.order;

import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.application.order.event.OrderLineSnapshot;
import me.gogradually.toycommerce.domain.order.ExpiredOrderCancellationTarget;
import me.gogradually.toycommerce.domain.order.OrderItem;
import me.gogradually.toycommerce.domain.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderExpirationServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-03-10T00:00:00Z"), ZoneId.of("Asia/Seoul"));
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    private OrderExpirationService orderExpirationService;

    @BeforeEach
    void setUp() {
        orderExpirationService = new OrderExpirationService(orderRepository, applicationEventPublisher, clock);
    }

    @Test
    void shouldCancelExpiredOrdersAndPublishEvents() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 9, 8, 0, 0);
        ExpiredOrderCancellationTarget createdOrder = expiredTarget(1L, createdAt);
        ExpiredOrderCancellationTarget infoCompletedOrder = expiredTarget(2L, createdAt);
        LocalDateTime expectedUpdatedAt = LocalDateTime.of(2026, 3, 10, 9, 0, 0);

        ReflectionTestUtils.setField(orderExpirationService, "expirationMinutes", 30L);
        when(orderRepository.findExpiredCancellationTargets(any(), any()))
                .thenReturn(List.of(createdOrder, infoCompletedOrder));
        when(orderRepository.cancelExpiredOrders(List.of(1L, 2L), expectedUpdatedAt)).thenReturn(2);

        int cancelledCount = orderExpirationService.cancelExpiredOrders();

        assertThat(cancelledCount).isEqualTo(2);
        verify(orderRepository).cancelExpiredOrders(List.of(1L, 2L), expectedUpdatedAt);
        verify(applicationEventPublisher, times(2)).publishEvent(any(OrderCancelledEvent.class));

        ArgumentCaptor<OrderCancelledEvent> eventCaptor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
        verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .extracting(OrderCancelledEvent::orderId)
                .containsExactly(1L, 2L);
        assertThat(eventCaptor.getAllValues())
                .flatExtracting(OrderCancelledEvent::items)
                .extracting(OrderLineSnapshot::productId)
                .containsOnly(11L);
    }

    @Test
    void shouldDoNothingWhenNoExpiredOrders() {
        ReflectionTestUtils.setField(orderExpirationService, "expirationMinutes", 30L);
        when(orderRepository.findExpiredCancellationTargets(any(), any())).thenReturn(List.of());

        int cancelledCount = orderExpirationService.cancelExpiredOrders();

        assertThat(cancelledCount).isZero();
        verify(orderRepository, never()).cancelExpiredOrders(anyList(), any());
        verify(applicationEventPublisher, never()).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    void shouldRollbackWhenBulkCancellationCountDoesNotMatchFetchedOrders() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 9, 8, 0, 0);
        ReflectionTestUtils.setField(orderExpirationService, "expirationMinutes", 30L);
        when(orderRepository.findExpiredCancellationTargets(any(), any()))
                .thenReturn(List.of(expiredTarget(1L, createdAt), expiredTarget(2L, createdAt)));
        when(orderRepository.cancelExpiredOrders(anyList(), any())).thenReturn(1);

        assertThatThrownBy(() -> orderExpirationService.cancelExpiredOrders())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expired order cancellation count mismatch.");
        verify(applicationEventPublisher, never()).publishEvent(any(OrderCancelledEvent.class));
    }

    private ExpiredOrderCancellationTarget expiredTarget(Long orderId, LocalDateTime createdAt) {
        OrderItem item = OrderItem.restore(
                1L,
                orderId,
                11L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                1,
                new BigDecimal("15900"),
                createdAt,
                createdAt
        );
        return new ExpiredOrderCancellationTarget(orderId, List.of(item));
    }
}

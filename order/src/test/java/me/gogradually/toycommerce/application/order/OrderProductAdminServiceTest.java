package me.gogradually.toycommerce.application.order;

import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.domain.order.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProductAdminServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrderProductAdminService orderProductAdminService;

    @Test
    void shouldCancelOpenOrdersContainingProductAndPublishEvents() {
        Order firstOrder = createdOrder(1L, 11L, 2);
        Order secondOrder = createdOrder(2L, 11L, 1);

        when(orderRepository.findOpenOrdersContainingProduct(11L)).thenReturn(List.of(firstOrder, secondOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int cancelledCount = orderProductAdminService.cancelOpenOrdersContainingProduct(11L);

        assertThat(cancelledCount).isEqualTo(2);

        ArgumentCaptor<OrderCancelledEvent> eventCaptor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
        verify(applicationEventPublisher, org.mockito.Mockito.times(2)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .extracting(OrderCancelledEvent::orderId)
                .containsExactly(1L, 2L);
        assertThat(eventCaptor.getAllValues())
                .flatExtracting(OrderCancelledEvent::items)
                .extracting("productId", "quantity")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(11L, 2),
                        org.assertj.core.groups.Tuple.tuple(11L, 1)
                );
    }

    @Test
    void shouldCheckWhetherProductIsReferencedByOrders() {
        when(orderRepository.existsAnyOrderItemByProductId(11L)).thenReturn(true);

        boolean exists = orderProductAdminService.existsAnyOrderItemByProductId(11L);

        assertThat(exists).isTrue();
    }

    private Order createdOrder(Long orderId, Long productId, int quantity) {
        LocalDateTime now = LocalDateTime.now();
        OrderItem item = OrderItem.restore(
                1L,
                orderId,
                productId,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                quantity,
                new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                now,
                now
        );

        return Order.restore(
                orderId,
                1001L,
                OrderStatus.CREATED,
                OrderDetails.empty(),
                item.getLineTotal(),
                BigDecimal.ZERO,
                item.getLineTotal(),
                List.of(item),
                now,
                now
        );
    }
}

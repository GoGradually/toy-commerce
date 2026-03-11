package me.gogradually.toycommerce.application.order;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.application.order.event.OrderLineSnapshot;
import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderItem;
import me.gogradually.toycommerce.domain.order.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderProductAdminService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public int cancelOpenOrdersContainingProduct(Long productId) {
        List<Order> openOrders = orderRepository.findOpenOrdersContainingProduct(productId);
        for (Order order : openOrders) {
            order.cancel();
            Order saved = orderRepository.save(order);
            applicationEventPublisher.publishEvent(new OrderCancelledEvent(saved.getId(), toSnapshots(saved.getItems())));
        }
        return openOrders.size();
    }

    public boolean existsAnyOrderItemByProductId(Long productId) {
        return orderRepository.existsAnyOrderItemByProductId(productId);
    }

    private List<OrderLineSnapshot> toSnapshots(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderLineSnapshot(item.getProductId(), item.getQuantity()))
                .toList();
    }
}

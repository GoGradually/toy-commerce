package me.gogradually.toycommerce.application.product;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.application.order.event.OrderCreatedEvent;
import me.gogradually.toycommerce.application.order.event.OrderLineSnapshot;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderProductStockEventHandler {

    private final ProductRepository productRepository;

    @EventListener
    public void handle(OrderCreatedEvent event) {
        for (OrderLineSnapshot orderItem : sortOrderItemsByProductId(event.items())) {
            Product product = productRepository.findByIdForUpdate(orderItem.productId())
                    .orElseThrow(() -> new ProductNotFoundException(orderItem.productId()));

            ensureProductIsActive(product);
            product.decreaseStock(orderItem.quantity());
            productRepository.save(product);
        }
    }

    @EventListener
    public void handle(OrderCancelledEvent event) {
        for (OrderLineSnapshot orderItem : sortOrderItemsByProductId(event.items())) {
            Product product = productRepository.findByIdForUpdate(orderItem.productId())
                    .orElseThrow(() -> new ProductNotFoundException(orderItem.productId()));

            product.increaseStock(orderItem.quantity());
            productRepository.save(product);
        }
    }

    private void ensureProductIsActive(Product product) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new InactiveCartProductException(product.getId(), product.getStatus());
        }
    }

    private List<OrderLineSnapshot> sortOrderItemsByProductId(List<OrderLineSnapshot> orderItems) {
        return orderItems.stream()
                .sorted(Comparator.comparing(OrderLineSnapshot::productId))
                .toList();
    }
}

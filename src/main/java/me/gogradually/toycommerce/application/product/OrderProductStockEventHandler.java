package me.gogradually.toycommerce.application.product;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.event.OrderCreatedEvent;
import me.gogradually.toycommerce.application.order.event.OrderPaymentFailedEvent;
import me.gogradually.toycommerce.domain.order.OrderItem;
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
        for (OrderItem orderItem : sortOrderItemsByProductId(event.items())) {
            Product product = productRepository.findByIdForUpdate(orderItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(orderItem.getProductId()));

            ensureProductIsActive(product);
            product.decreaseStock(orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    @EventListener
    public void handle(OrderPaymentFailedEvent event) {
        for (OrderItem orderItem : sortOrderItemsByProductId(event.items())) {
            Product product = productRepository.findByIdForUpdate(orderItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(orderItem.getProductId()));

            product.increaseStock(orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    private void ensureProductIsActive(Product product) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new InactiveCartProductException(product.getId(), product.getStatus());
        }
    }

    private List<OrderItem> sortOrderItemsByProductId(List<OrderItem> orderItems) {
        return orderItems.stream()
                .sorted(Comparator.comparing(OrderItem::getProductId))
                .toList();
    }
}

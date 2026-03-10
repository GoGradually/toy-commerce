package me.gogradually.toycommerce.application.product;

import me.gogradually.toycommerce.application.order.event.OrderCancelledEvent;
import me.gogradually.toycommerce.application.order.event.OrderCreatedEvent;
import me.gogradually.toycommerce.domain.order.OrderItem;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class OrderProductStockEventHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderProductStockEventHandler handler;

    @Test
    void shouldDecreaseStockInAscendingProductIdOrder() {
        OrderItem firstItem = orderItem(1L, 1L, 20L, 1);
        OrderItem secondItem = orderItem(2L, 1L, 11L, 2);
        Product firstProduct = activeProduct(11L, 10);
        Product secondProduct = activeProduct(20L, 10);

        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(secondProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        handler.handle(new OrderCreatedEvent(1L, List.of(firstItem, secondItem)));

        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository, times(2)).findByIdForUpdate(productIdCaptor.capture());
        assertThat(productIdCaptor.getAllValues()).containsExactly(11L, 20L);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(2)).save(productCaptor.capture());
        assertThat(productCaptor.getAllValues())
                .extracting(Product::getStock)
                .containsExactly(8, 9);
    }

    @Test
    void shouldRestoreStockInAscendingProductIdOrderWhenOrderCancelled() {
        OrderItem firstItem = orderItem(1L, 1L, 20L, 1);
        OrderItem secondItem = orderItem(2L, 1L, 11L, 2);
        Product firstProduct = activeProduct(11L, 8);
        Product secondProduct = activeProduct(20L, 9);

        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(secondProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        handler.handle(new OrderCancelledEvent(1L, List.of(firstItem, secondItem)));

        ArgumentCaptor<Long> productIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository, times(2)).findByIdForUpdate(productIdCaptor.capture());
        assertThat(productIdCaptor.getAllValues()).containsExactly(11L, 20L);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(2)).save(productCaptor.capture());
        assertThat(productCaptor.getAllValues())
                .extracting(Product::getStock)
                .containsExactly(10, 10);
    }

    private Product activeProduct(Long id, int stock) {
        return Product.restore(
                id,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                stock,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2)
        );
    }

    private OrderItem orderItem(Long id, Long orderId, Long productId, int quantity) {
        return OrderItem.restore(
                id,
                orderId,
                productId,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                quantity,
                new BigDecimal("15900").multiply(BigDecimal.valueOf(quantity)),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(2)
        );
    }
}

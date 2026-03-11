package me.gogradually.toycommerce.infrastructure;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.port.ProductSnapshot;
import me.gogradually.toycommerce.application.order.port.ProductSnapshotPort;
import me.gogradually.toycommerce.domain.order.exception.InactiveOrderProductException;
import me.gogradually.toycommerce.domain.order.exception.OrderProductNotFoundException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductSnapshotPortAdapter implements ProductSnapshotPort {

    private final ProductRepository productRepository;

    @Override
    public ProductSnapshot getActiveProduct(Long productId) {
        return findActiveProduct(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new OrderProductNotFoundException(productId));
                    throw new InactiveOrderProductException(product.getId(), product.getStatus().name());
                });
    }

    @Override
    public Optional<ProductSnapshot> findActiveProduct(Long productId) {
        return productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
                .map(this::toSnapshot);
    }

    private ProductSnapshot toSnapshot(Product product) {
        return new ProductSnapshot(product.getId(), product.getName(), product.getPrice());
    }
}

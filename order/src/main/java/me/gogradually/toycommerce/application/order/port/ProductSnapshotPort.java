package me.gogradually.toycommerce.application.order.port;

import java.util.Optional;

public interface ProductSnapshotPort {

    ProductSnapshot getActiveProduct(Long productId);

    Optional<ProductSnapshot> findActiveProduct(Long productId);
}

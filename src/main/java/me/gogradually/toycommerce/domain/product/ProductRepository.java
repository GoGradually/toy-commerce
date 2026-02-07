package me.gogradually.toycommerce.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long productId);

    Optional<Product> findByIdForUpdate(Long productId);

    Optional<Product> findByIdAndStatus(Long productId, ProductStatus status);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    void deleteById(Long productId);
}

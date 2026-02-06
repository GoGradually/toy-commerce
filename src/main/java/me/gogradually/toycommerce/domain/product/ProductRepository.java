package me.gogradually.toycommerce.domain.product;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long productId);

    Optional<Product> findByIdAndStatus(Long productId, ProductStatus status);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    void deleteById(Long productId);
}

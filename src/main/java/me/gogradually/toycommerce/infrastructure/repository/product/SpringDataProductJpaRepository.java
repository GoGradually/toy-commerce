package me.gogradually.toycommerce.infrastructure.repository.product;

import java.util.Optional;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    Optional<ProductJpaEntity> findByIdAndStatus(Long id, ProductStatus status);

    Page<ProductJpaEntity> findByStatus(ProductStatus status, Pageable pageable);
}

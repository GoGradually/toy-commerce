package me.gogradually.toycommerce.infrastructure.repository.product;

import jakarta.persistence.LockModeType;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductJpaEntity p where p.id = :id")
    Optional<ProductJpaEntity> findByIdForUpdate(@Param("id") Long id);

    Optional<ProductJpaEntity> findByIdAndStatus(Long id, ProductStatus status);

    Page<ProductJpaEntity> findByStatus(ProductStatus status, Pageable pageable);
}

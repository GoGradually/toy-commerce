package me.gogradually.toycommerce.infrastructure.repository.product;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.from(product);
        ProductJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return jpaRepository.findById(productId)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    public Optional<Product> findByIdAndStatus(Long productId, ProductStatus status) {
        return jpaRepository.findByIdAndStatus(productId, status)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    public Page<Product> findByStatus(ProductStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    public void deleteById(Long productId) {
        jpaRepository.deleteById(productId);
    }
}

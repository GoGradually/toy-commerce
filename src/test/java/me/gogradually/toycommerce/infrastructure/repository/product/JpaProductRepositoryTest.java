package me.gogradually.toycommerce.infrastructure.repository.product;

import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaProductRepositoryTest {

    @Mock
    private SpringDataProductJpaRepository jpaRepository;

    @InjectMocks
    private JpaProductRepository productRepository;

    @Test
    void shouldSaveProductWithEntityMapping() {
        Product product = restoreProduct(1L, ProductStatus.ACTIVE);
        when(jpaRepository.save(any(ProductJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product saved = productRepository.save(product);

        ArgumentCaptor<ProductJpaEntity> captor = ArgumentCaptor.forClass(ProductJpaEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getName()).isEqualTo("레고 스타터 세트");
        assertThat(saved.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldFindByIdWithDomainMapping() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(ProductJpaEntity.from(restoreProduct(1L, ProductStatus.ACTIVE))));

        Optional<Product> result = productRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void shouldFindByIdForUpdateWithDomainMapping() {
        when(jpaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ProductJpaEntity.from(restoreProduct(1L, ProductStatus.ACTIVE))));

        Optional<Product> result = productRepository.findByIdForUpdate(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldFindByIdAndStatusWithDomainMapping() {
        when(jpaRepository.findByIdAndStatus(1L, ProductStatus.INACTIVE))
                .thenReturn(Optional.of(ProductJpaEntity.from(restoreProduct(1L, ProductStatus.INACTIVE))));

        Optional<Product> result = productRepository.findByIdAndStatus(1L, ProductStatus.INACTIVE);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    void shouldFindByStatusWithPageMapping() {
        Product product = restoreProduct(1L, ProductStatus.ACTIVE);
        PageRequest pageable = PageRequest.of(0, 20);
        Page<ProductJpaEntity> entityPage = new PageImpl<>(List.of(ProductJpaEntity.from(product)), pageable, 1);
        when(jpaRepository.findByStatus(ProductStatus.ACTIVE, pageable)).thenReturn(entityPage);

        Page<Product> result = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("레고 스타터 세트");
    }

    @Test
    void shouldDelegateDeleteById() {
        productRepository.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }

    private Product restoreProduct(Long id, ProductStatus status) {
        LocalDateTime now = LocalDateTime.of(2026, 2, 7, 18, 5, 0);
        return Product.restore(
                id,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                10,
                status,
                now.minusDays(1),
                now
        );
    }
}


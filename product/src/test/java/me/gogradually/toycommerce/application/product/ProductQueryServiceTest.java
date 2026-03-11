package me.gogradually.toycommerce.application.product;

import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.application.product.dto.ProductPageInfo;
import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductQueryService productQueryService;

    @Test
    void shouldGetActiveProductList() {
        Product product = Product.restore(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(5)
        );

        Page<Product> page = new PageImpl<>(
                List.of(product),
                PageRequest.of(0, 20),
                1
        );

        when(productRepository.findByStatus(org.mockito.ArgumentMatchers.eq(ProductStatus.ACTIVE), any())).thenReturn(page);

        ProductPageInfo result = productQueryService.getProducts(0, 20, "createdAt", "desc");

        assertThat(result.products()).hasSize(1);
        assertThat(result.products().getFirst().id()).isEqualTo(1L);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldGetActiveProductDetail() {
        Product product = Product.restore(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(5)
        );

        when(productRepository.findByIdAndStatus(1L, ProductStatus.ACTIVE)).thenReturn(Optional.of(product));

        ProductDetailInfo result = productQueryService.getProduct(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("레고 스타터 세트");
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productQueryService.getProduct(100L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWhenSortByIsInvalid() {
        assertThatThrownBy(() -> productQueryService.getProducts(0, 20, "unknown", "desc"))
                .isInstanceOf(InvalidProductQueryException.class);
    }
}

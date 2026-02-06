package me.gogradually.toycommerce.application.product;

import me.gogradually.toycommerce.application.product.command.CreateProductCommand;
import me.gogradually.toycommerce.application.product.command.UpdateProductCommand;
import me.gogradually.toycommerce.application.product.command.UpdateProductStockCommand;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AdminProductService adminProductService;

    @Test
    void shouldCreateProduct() {
        CreateProductCommand command = new CreateProductCommand(
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE
        );

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            LocalDateTime now = LocalDateTime.now();
            return Product.restore(1L, product.getName(), product.getPrice(), product.getStock(), product.getStatus(), now, now);
        });

        ProductDetailInfo result = adminProductService.createProduct(command);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("레고 스타터 세트");
        assertThat(result.price()).isEqualByComparingTo("15900");
        assertThat(result.stock()).isEqualTo(50);
        assertThat(result.status()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldUpdateProduct() {
        Product existing = Product.restore(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        UpdateProductCommand command = new UpdateProductCommand(
                "레고 프로 세트",
                new BigDecimal("25900"),
                ProductStatus.INACTIVE
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDetailInfo result = adminProductService.updateProduct(1L, command);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("레고 프로 세트");
        assertThat(result.price()).isEqualByComparingTo("25900");
        assertThat(result.status()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    void shouldUpdateStock() {
        Product existing = Product.restore(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDetailInfo result = adminProductService.updateStock(1L, new UpdateProductStockCommand(12));

        assertThat(result.stock()).isEqualTo(12);
    }

    @Test
    void shouldDeleteProduct() {
        Product existing = Product.restore(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        adminProductService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminProductService.updateStock(100L, new UpdateProductStockCommand(10)))
                .isInstanceOf(ProductNotFoundException.class);
    }
}

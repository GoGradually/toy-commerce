package me.gogradually.toycommerce.domain.product;

import me.gogradually.toycommerce.domain.product.exception.InsufficientProductStockException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductNameException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductPriceException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void shouldCreateProductWhenValidValues() {
        Product product = Product.create("레고", new BigDecimal("19900"), 10, ProductStatus.ACTIVE);

        assertThat(product.getName()).isEqualTo("레고");
        assertThat(product.getPrice()).isEqualByComparingTo("19900");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        assertThatThrownBy(() -> Product.create("레고", new BigDecimal("-1"), 10, ProductStatus.ACTIVE))
                .isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    void shouldThrowWhenStockIsNegative() {
        assertThatThrownBy(() -> Product.create("레고", new BigDecimal("1000"), -1, ProductStatus.ACTIVE))
                .isInstanceOf(InvalidProductStockException.class);
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() -> Product.create(" ", new BigDecimal("1000"), 1, ProductStatus.ACTIVE))
                .isInstanceOf(InvalidProductNameException.class);
    }

    @Test
    void shouldDecreaseStock() {
        Product product = Product.create("레고", new BigDecimal("19900"), 10, ProductStatus.ACTIVE);

        product.decreaseStock(3);

        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    void shouldIncreaseStock() {
        Product product = Product.create("레고", new BigDecimal("19900"), 10, ProductStatus.ACTIVE);

        product.increaseStock(4);

        assertThat(product.getStock()).isEqualTo(14);
    }

    @Test
    void shouldThrowWhenStockIsInsufficient() {
        Product product = Product.create("레고", new BigDecimal("19900"), 2, ProductStatus.ACTIVE);

        assertThatThrownBy(() -> product.decreaseStock(3))
                .isInstanceOf(InsufficientProductStockException.class);
    }
}

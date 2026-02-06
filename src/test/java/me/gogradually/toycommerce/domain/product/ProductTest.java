package me.gogradually.toycommerce.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import org.junit.jupiter.api.Test;

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
                .isInstanceOf(ToyCommerceException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_PRICE);
    }

    @Test
    void shouldThrowWhenStockIsNegative() {
        assertThatThrownBy(() -> Product.create("레고", new BigDecimal("1000"), -1, ProductStatus.ACTIVE))
                .isInstanceOf(ToyCommerceException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_STOCK);
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() -> Product.create(" ", new BigDecimal("1000"), 1, ProductStatus.ACTIVE))
                .isInstanceOf(ToyCommerceException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRODUCT_NAME);
    }
}

package me.gogradually.toycommerce.interfaces.utils;

import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartQuantityException;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.InactiveProductException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductPriceException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToyCommerceExceptionErrorCodeMapperTest {

    private final ToyCommerceExceptionErrorCodeMapper mapper = new ToyCommerceExceptionErrorCodeMapper();

    @Test
    void shouldMapProductNotFound() {
        ErrorCode result = mapper.map(new ProductNotFoundException(1L));

        assertThat(result).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void shouldMapInvalidProductPrice() {
        ErrorCode result = mapper.map(new InvalidProductPriceException(null));

        assertThat(result).isEqualTo(ErrorCode.INVALID_PRODUCT_PRICE);
    }

    @Test
    void shouldMapInvalidProductQuery() {
        ErrorCode result = mapper.map(InvalidProductQueryException.invalidSortBy("x"));

        assertThat(result).isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    @Test
    void shouldMapInactiveProduct() {
        ErrorCode result = mapper.map(new InactiveProductException(1L, ProductStatus.INACTIVE));

        assertThat(result).isEqualTo(ErrorCode.INACTIVE_PRODUCT);
    }

    @Test
    void shouldMapInvalidCartQuantity() {
        ErrorCode result = mapper.map(new InvalidCartQuantityException(0));

        assertThat(result).isEqualTo(ErrorCode.INVALID_CART_QUANTITY);
    }

    @Test
    void shouldMapInactiveCartProduct() {
        ErrorCode result = mapper.map(new InactiveCartProductException(1L, ProductStatus.INACTIVE));

        assertThat(result).isEqualTo(ErrorCode.INACTIVE_CART_PRODUCT);
    }

    @Test
    void shouldFallbackToInternalServerErrorForUnknownBusinessException() {
        ErrorCode result = mapper.map(new UnknownBusinessException());

        assertThat(result).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private static class UnknownBusinessException extends ToyCommerceException {

        private UnknownBusinessException() {
            super("Unknown business exception for test");
        }
    }
}

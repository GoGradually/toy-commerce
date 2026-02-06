package me.gogradually.toycommerce.interfaces.utils;

import static org.assertj.core.api.Assertions.assertThat;

import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductPriceException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import org.junit.jupiter.api.Test;

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

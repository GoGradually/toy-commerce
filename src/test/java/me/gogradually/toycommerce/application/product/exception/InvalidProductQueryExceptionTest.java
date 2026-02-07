package me.gogradually.toycommerce.application.product.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidProductQueryExceptionTest {

    @Test
    void shouldCreateExceptionForInvalidPage() {
        InvalidProductQueryException exception = InvalidProductQueryException.invalidPage(-1);

        assertThat(exception.getDebugMessage()).isEqualTo("Invalid product query parameter.");
        assertThat(exception.getDebugContext())
                .containsEntry("parameter", "page")
                .containsEntry("value", "-1")
                .containsEntry("reason", "must be greater than or equal to 0");
    }

    @Test
    void shouldCreateExceptionForInvalidSize() {
        InvalidProductQueryException exception = InvalidProductQueryException.invalidSize(101);

        assertThat(exception.getDebugMessage()).isEqualTo("Invalid product query parameter.");
        assertThat(exception.getDebugContext())
                .containsEntry("parameter", "size")
                .containsEntry("value", "101")
                .containsEntry("reason", "must be between 1 and 100");
    }

    @Test
    void shouldCreateExceptionForInvalidSortBy() {
        InvalidProductQueryException exception = InvalidProductQueryException.invalidSortBy("updatedAt");

        assertThat(exception.getDebugMessage()).isEqualTo("Invalid product query parameter.");
        assertThat(exception.getDebugContext())
                .containsEntry("parameter", "sortBy")
                .containsEntry("value", "updatedAt")
                .containsEntry("reason", "must be one of id,name,price,createdAt");
    }

    @Test
    void shouldCreateExceptionForInvalidDirection() {
        InvalidProductQueryException exception = InvalidProductQueryException.invalidDirection("down");

        assertThat(exception.getDebugMessage()).isEqualTo("Invalid product query parameter.");
        assertThat(exception.getDebugContext())
                .containsEntry("parameter", "direction")
                .containsEntry("value", "down")
                .containsEntry("reason", "must be asc or desc");
    }
}


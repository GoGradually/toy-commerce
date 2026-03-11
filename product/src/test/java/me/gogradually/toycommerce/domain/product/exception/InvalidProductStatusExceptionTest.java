package me.gogradually.toycommerce.domain.product.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidProductStatusExceptionTest {

    @Test
    void shouldContainDebugContextWhenStatusIsInvalid() {
        InvalidProductStatusException exception = new InvalidProductStatusException(null);

        assertThat(exception.getDebugMessage()).isEqualTo("Product status must not be null.");
        assertThat(exception.getDebugContext()).containsEntry("status", "null");
    }
}


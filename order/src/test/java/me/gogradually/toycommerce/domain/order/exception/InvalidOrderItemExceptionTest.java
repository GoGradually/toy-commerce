package me.gogradually.toycommerce.domain.order.exception;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidOrderItemExceptionTest {

    @Test
    void shouldCreateExceptionForInvalidOrderId() {
        InvalidOrderItemException exception = InvalidOrderItemException.invalidOrderId(0L);

        assertThat(exception.getDebugMessage()).isEqualTo("Order item orderId must be greater than 0.");
        assertThat(exception.getDebugContext()).containsEntry("orderId", "0");
    }

    @Test
    void shouldCreateExceptionForInvalidProductId() {
        InvalidOrderItemException exception = InvalidOrderItemException.invalidProductId(0L);

        assertThat(exception.getDebugMessage()).isEqualTo("Order item productId must be greater than 0.");
        assertThat(exception.getDebugContext()).containsEntry("productId", "0");
    }

    @Test
    void shouldCreateExceptionForInvalidProductName() {
        InvalidOrderItemException exception = InvalidOrderItemException.invalidProductName(" ");

        assertThat(exception.getDebugMessage()).isEqualTo("Order item productNameSnapshot must not be blank.");
        assertThat(exception.getDebugContext()).containsEntry("productNameSnapshot", " ");
    }

    @Test
    void shouldCreateExceptionForInvalidUnitPrice() {
        InvalidOrderItemException exception = InvalidOrderItemException.invalidUnitPrice(new BigDecimal("-1"));

        assertThat(exception.getDebugMessage()).isEqualTo("Order item unitPrice must be zero or positive.");
        assertThat(exception.getDebugContext()).containsEntry("unitPrice", "-1");
    }

    @Test
    void shouldCreateExceptionForInvalidQuantity() {
        InvalidOrderItemException exception = InvalidOrderItemException.invalidQuantity(0);

        assertThat(exception.getDebugMessage()).isEqualTo("Order item quantity must be greater than 0.");
        assertThat(exception.getDebugContext()).containsEntry("quantity", "0");
    }

    @Test
    void shouldCreateExceptionForInvalidLineTotal() {
        InvalidOrderItemException exception = InvalidOrderItemException.invalidLineTotal(
                new BigDecimal("100.00"),
                new BigDecimal("90.00")
        );

        assertThat(exception.getDebugMessage()).isEqualTo("Order item lineTotal must match unitPrice * quantity.");
        assertThat(exception.getDebugContext())
                .containsEntry("expectedLineTotal", "100.00")
                .containsEntry("lineTotal", "90.00");
    }
}


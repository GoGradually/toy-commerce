package me.gogradually.toycommerce.domain.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderValidationExceptionsTest {

    @Test
    void shouldExposeDebugContextForInvalidOrderAddressException() {
        InvalidOrderAddressException exception = new InvalidOrderAddressException("addressLine1", " ");

        assertThat(exception.getMessage()).isEqualTo("Order address field is invalid.");
        assertThat(exception.hasDebugContext()).isTrue();
        assertThat(exception.getDebugContext())
                .containsEntry("fieldName", "addressLine1")
                .containsEntry("value", " ");
    }

    @Test
    void shouldExposeDebugContextForInvalidOrderZipCodeException() {
        InvalidOrderZipCodeException exception = new InvalidOrderZipCodeException("12");

        assertThat(exception.getMessage()).isEqualTo("Order zipCode is invalid.");
        assertThat(exception.getDebugContext()).containsEntry("zipCode", "12");
    }

    @Test
    void shouldExposeDebugContextForInvalidOrderReceiverNameException() {
        InvalidOrderReceiverNameException exception = new InvalidOrderReceiverNameException(" ");

        assertThat(exception.getMessage()).isEqualTo("Order receiverName must not be blank.");
        assertThat(exception.getDebugContext()).containsEntry("receiverName", " ");
    }

    @Test
    void shouldExposeDebugContextForInvalidOrderReceiverPhoneException() {
        InvalidOrderReceiverPhoneException exception = new InvalidOrderReceiverPhoneException("abc");

        assertThat(exception.getMessage()).isEqualTo("Order receiverPhone is invalid.");
        assertThat(exception.getDebugContext()).containsEntry("receiverPhone", "abc");
    }

    @Test
    void shouldExposeDebugContextForInvalidOrderPaymentMethodException() {
        InvalidOrderPaymentMethodException exception = new InvalidOrderPaymentMethodException(null);

        assertThat(exception.getMessage()).isEqualTo("Order paymentMethod must not be null.");
        assertThat(exception.getDebugContext()).containsEntry("paymentMethod", "null");
    }
}

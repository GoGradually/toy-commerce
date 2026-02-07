package me.gogradually.toycommerce.domain.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidOrderMemberIdExceptionTest {

    @Test
    void shouldContainDebugContextWhenMemberIdIsInvalid() {
        InvalidOrderMemberIdException exception = new InvalidOrderMemberIdException(0L);

        assertThat(exception.getDebugMessage()).isEqualTo("Order memberId must be greater than 0.");
        assertThat(exception.getDebugContext()).containsEntry("memberId", "0");
    }
}


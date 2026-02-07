package me.gogradually.toycommerce.interfaces.utils.exception;

import me.gogradually.toycommerce.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationErrorMessageResolverTest {

    private final ValidationErrorMessageResolver resolver = new ValidationErrorMessageResolver();

    private static MethodParameter methodParameter() {
        try {
            Method method = DummyController.class.getDeclaredMethod("handle", DummyPayload.class);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Test
    void shouldResolveMethodArgumentNotValidMessage() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyPayload(), "payload");
        bindingResult.rejectValue("name", "NotBlank", "name은 필수입니다.");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter(), bindingResult);

        String message = resolver.resolve(exception);

        assertThat(message).isEqualTo("name은 필수입니다.");
    }

    @Test
    void shouldFallbackWhenMethodArgumentNotValidHasNoFieldError() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyPayload(), "payload");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter(), bindingResult);

        String message = resolver.resolve(exception);

        assertThat(message).isEqualTo(ErrorCode.INVALID_REQUEST.getMessage());
    }

    @Test
    void shouldResolveBindExceptionFieldMessage() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyPayload(), "payload");
        bindingResult.rejectValue("name", "NotBlank", "name은 비어 있을 수 없습니다.");
        BindException exception = new BindException(bindingResult);

        String message = resolver.resolve(exception);

        assertThat(message).isEqualTo("name은 비어 있을 수 없습니다.");
    }

    @Test
    void shouldFallbackWhenBindExceptionHasNoFieldError() {
        BindException exception = new BindException(new DummyPayload(), "payload");

        String message = resolver.resolve(exception);

        assertThat(message).isEqualTo(ErrorCode.INVALID_REQUEST.getMessage());
    }

    @Test
    void shouldFallbackForUnsupportedExceptionType() {
        String message = resolver.resolve(new IllegalArgumentException("invalid"));

        assertThat(message).isEqualTo(ErrorCode.INVALID_REQUEST.getMessage());
    }

    private static class DummyController {
        @SuppressWarnings("unused")
        void handle(DummyPayload payload) {
        }
    }

    private static class DummyPayload {
        @SuppressWarnings("unused")
        private String name;

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }
    }
}


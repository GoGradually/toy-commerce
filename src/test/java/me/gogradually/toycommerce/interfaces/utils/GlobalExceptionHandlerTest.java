package me.gogradually.toycommerce.interfaces.utils;

import jakarta.servlet.http.HttpServletRequest;
import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import me.gogradually.toycommerce.interfaces.utils.exception.ValidationErrorMessageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ToyCommerceExceptionErrorCodeMapper errorCodeMapper;

    @Mock
    private ValidationErrorMessageResolver validationErrorMessageResolver;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void shouldHandleToyCommerceExceptionWithMappedErrorCode() {
        ToyCommerceException exception = InvalidProductQueryException.invalidPage(-1);
        when(errorCodeMapper.map(exception)).thenReturn(ErrorCode.INVALID_REQUEST);
        when(request.getRequestURI()).thenReturn("/api/products");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleToyCommerceException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().error().code()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
        assertThat(response.getBody().error().message()).isEqualTo(ErrorCode.INVALID_REQUEST.getMessage());
        verify(errorCodeMapper).map(exception);
        verify(request).getRequestURI();
    }

    @Test
    void shouldHandleInvalidRequestWithResolvedMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("invalid");
        when(validationErrorMessageResolver.resolve(exception)).thenReturn("요청 값이 올바르지 않습니다.");

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleInvalidRequest(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().error().code()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
        assertThat(response.getBody().error().message()).isEqualTo("요청 값이 올바르지 않습니다.");
        verify(validationErrorMessageResolver).resolve(exception);
    }

    @Test
    void shouldHandleUnexpectedExceptionAsInternalServerError() {
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleUnexpectedException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().error().code()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(response.getBody().error().message()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}


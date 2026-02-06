package me.gogradually.toycommerce.interfaces.utils;

import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import me.gogradually.toycommerce.interfaces.utils.exception.ValidationErrorMessageResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ToyCommerceExceptionErrorCodeMapper errorCodeMapper;
    private final ValidationErrorMessageResolver validationErrorMessageResolver;

    @ExceptionHandler(ToyCommerceException.class)
    public ResponseEntity<ApiResponse<Void>> handleToyCommerceException(
            ToyCommerceException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = errorCodeMapper.map(exception);
        log.warn(
                "Handled business exception. type={}, code={}, path={}, debugMessage={}, debugContext={}",
                exception.getClass().getSimpleName(),
                errorCode.getCode(),
                request.getRequestURI(),
                exception.getDebugMessage(),
                exception.getDebugContext()
        );
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(Exception exception) {
        String message = validationErrorMessageResolver.resolve(exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ErrorCode.INVALID_REQUEST.getCode(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}

package me.gogradually.toycommerce.interfaces.utils.exception;

import me.gogradually.toycommerce.common.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ValidationErrorMessageResolver {

    public String resolve(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            FieldError fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
            if (fieldError != null) {
                return fieldError.getDefaultMessage();
            }
        }

        if (exception instanceof BindException bindException) {
            FieldError fieldError = bindException.getBindingResult().getFieldError();
            if (fieldError != null) {
                return fieldError.getDefaultMessage();
            }
        }

        return ErrorCode.INVALID_REQUEST.getMessage();
    }
}

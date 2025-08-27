package com.Chicken.project.exception;

import com.Chicken.project.dto.response.ApiResponse;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException e) {
        Locale locale = LocaleContextHolder.getLocale();

        String message = messageSource.getMessage(e.getErrorCode(),null, locale);
        ApiResponse<Object> apiResponse = new ApiResponse<>(e.getErrorCode(), message, null);
        HttpStatus status;
        if (e.getErrorCode() != null && e.getErrorCode().contains(".notFound")) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(apiResponse, status);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String code = error.getDefaultMessage();
            if (code.startsWith("{") && code.endsWith("}")) {
                code = code.substring(1, code.length() - 1);
            }
            String localizedMessage = messageSource.getMessage(code, null, code, locale);

            errors.put(error.getField(), localizedMessage);

        });
        ApiResponse<Object> apiResponse = new ApiResponse<>(
                "error.validation",
                messageSource.getMessage("error.validation", null, "Validation failed", locale),
                errors
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}

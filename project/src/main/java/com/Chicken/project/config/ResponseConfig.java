package com.Chicken.project.config;

import com.Chicken.project.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ResponseConfig {
    private final MessageSource messageSource;
    public <T> ResponseEntity<ApiResponse<T>> success(String code, String messageCode, Object[] args, T data) {
        String message = messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(new ApiResponse<>(code, message, data));
    }
    public <T> ResponseEntity<ApiResponse<T>> error(String code, String messageCode) {
        String message = messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
        return ResponseEntity.badRequest().body(new ApiResponse<>(code, message, null));
    }
    public <T> ResponseEntity<ApiResponse<T>> unauthorized(String code, String messageCode) {
        String message = messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(code, message, null));
    }
    public ResponseEntity<InputStreamResource> downloadFile(String fileName, ByteArrayInputStream data) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(data));
    }

}

package com.tengan.mall.media.interfaces.rest;

import com.tengan.mall.media.domain.exception.BannerNotFoundException;
import com.tengan.mall.media.domain.exception.FileTooLargeException;
import com.tengan.mall.media.domain.exception.UnsupportedCategoryException;
import com.tengan.mall.media.domain.exception.UnsupportedFileTypeException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MediaExceptionHandler {

    @ExceptionHandler(BannerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({FileTooLargeException.class, UnsupportedFileTypeException.class,
            UnsupportedCategoryException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}

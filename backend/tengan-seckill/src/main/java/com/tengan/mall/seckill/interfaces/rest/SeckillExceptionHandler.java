package com.tengan.mall.seckill.interfaces.rest;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.exception.ActivityStatusTransitionNotAllowedException;
import com.tengan.mall.seckill.domain.exception.SeckillNotActiveException;
import com.tengan.mall.seckill.domain.exception.SeckillPurchaseLimitExceededException;
import com.tengan.mall.seckill.domain.exception.SeckillSoldOutException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SeckillExceptionHandler {

    @ExceptionHandler({ActivityNotFoundException.class, SeckillNotActiveException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({SeckillSoldOutException.class, SeckillPurchaseLimitExceededException.class,
            ActivityStatusTransitionNotAllowedException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}

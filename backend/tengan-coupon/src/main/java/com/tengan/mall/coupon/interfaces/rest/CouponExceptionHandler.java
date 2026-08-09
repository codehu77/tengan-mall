package com.tengan.mall.coupon.interfaces.rest;

import com.tengan.mall.coupon.domain.exception.CouponAlreadyConsumedException;
import com.tengan.mall.coupon.domain.exception.CouponTemplateNotFoundException;
import com.tengan.mall.coupon.domain.exception.MemberCouponNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CouponExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對——跟 Spring Security 驗 Service JWT 失敗回應同一種狀態碼。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({CouponTemplateNotFoundException.class, MemberCouponNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(CouponAlreadyConsumedException.class)
    public ResponseEntity<Map<String, String>> handleConflict(CouponAlreadyConsumedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}

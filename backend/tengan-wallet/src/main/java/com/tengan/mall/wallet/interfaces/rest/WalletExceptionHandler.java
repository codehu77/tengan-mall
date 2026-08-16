package com.tengan.mall.wallet.interfaces.rest;

import com.tengan.mall.wallet.domain.exception.InsufficientPointsException;
import com.tengan.mall.wallet.domain.exception.PointsTransactionNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WalletExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對——跟 Spring Security 驗 Service JWT 失敗回應同一種狀態碼。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(PointsTransactionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(PointsTransactionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InsufficientPointsException.class)
    public ResponseEntity<Map<String, String>> handleConflict(InsufficientPointsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}

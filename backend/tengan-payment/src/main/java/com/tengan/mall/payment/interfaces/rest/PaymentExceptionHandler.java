package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.domain.exception.InvalidCheckMacValueException;
import com.tengan.mall.payment.domain.exception.OrderNotPayableException;
import com.tengan.mall.payment.domain.exception.PaymentAlreadyPaidException;
import com.tengan.mall.payment.domain.exception.PaymentGatewayException;
import com.tengan.mall.payment.domain.exception.PaymentRecordNotFoundException;
import com.tengan.mall.payment.domain.exception.PaymentTransactionMismatchException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對，跟 Spring Security 驗 Service JWT 失敗回應同一種狀態碼。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(PaymentRecordNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({PaymentAlreadyPaidException.class, OrderNotPayableException.class,
            PaymentTransactionMismatchException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InvalidCheckMacValueException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<Map<String, String>> handleGatewayError(PaymentGatewayException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", e.getMessage()));
    }
}

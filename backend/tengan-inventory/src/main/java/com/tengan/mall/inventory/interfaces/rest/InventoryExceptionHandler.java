package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.domain.exception.InsufficientStockException;
import com.tengan.mall.inventory.domain.exception.NegativeStockException;
import com.tengan.mall.inventory.domain.exception.PurchaseOrderAlreadyReceivedException;
import com.tengan.mall.inventory.domain.exception.PurchaseOrderNotFoundException;
import com.tengan.mall.inventory.domain.exception.WareSkuAlreadyExistsException;
import com.tengan.mall.inventory.domain.exception.WareSkuNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對——跟 Spring Security 驗 Service JWT 失敗回應同一種狀態碼。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({WareSkuNotFoundException.class, PurchaseOrderNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({InsufficientStockException.class, NegativeStockException.class,
            WareSkuAlreadyExistsException.class, PurchaseOrderAlreadyReceivedException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}

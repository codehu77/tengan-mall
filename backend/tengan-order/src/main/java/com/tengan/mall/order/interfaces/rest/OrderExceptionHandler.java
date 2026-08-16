package com.tengan.mall.order.interfaces.rest;

import com.tengan.mall.order.domain.exception.CouponNotApplicableException;
import com.tengan.mall.order.domain.exception.EmptyCartException;
import com.tengan.mall.order.domain.exception.InventoryShortageException;
import com.tengan.mall.order.domain.exception.OrderAccessDeniedException;
import com.tengan.mall.order.domain.exception.OrderCancellationNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderMarkPaidNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import com.tengan.mall.order.domain.exception.OrderReceiptNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderShipmentNotAllowedException;
import com.tengan.mall.order.domain.exception.OrderTokenInvalidException;
import com.tengan.mall.order.interfaces.rest.dto.InventoryShortageResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對——跟 Spring Security 驗 Service JWT 失敗回應同一種狀態碼。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    /** OrderAccessDeniedException 也回 404——不洩漏「訂單存在但不是你的」。 */
    @ExceptionHandler({OrderNotFoundException.class, OrderAccessDeniedException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InventoryShortageException.class)
    public ResponseEntity<InventoryShortageResponse> handleInventoryShortage(InventoryShortageException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new InventoryShortageResponse(e.getMessage(), e.getShortageSkuIds()));
    }

    @ExceptionHandler({OrderTokenInvalidException.class, EmptyCartException.class,
            CouponNotApplicableException.class, OrderCancellationNotAllowedException.class,
            OrderShipmentNotAllowedException.class, OrderReceiptNotAllowedException.class,
            OrderMarkPaidNotAllowedException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}

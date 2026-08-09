package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.domain.exception.AdminAccountDisabledException;
import com.tengan.mall.admin.domain.exception.DuplicateAdminUsernameException;
import com.tengan.mall.admin.domain.exception.DuplicateRoleCodeException;
import com.tengan.mall.admin.domain.exception.FileTooLargeException;
import com.tengan.mall.admin.domain.exception.InvalidAdminCredentialsException;
import com.tengan.mall.admin.domain.exception.InvalidAdminRefreshTokenException;
import com.tengan.mall.admin.domain.exception.InvalidMenuHierarchyException;
import com.tengan.mall.admin.domain.exception.InvalidMenuRouteException;
import com.tengan.mall.admin.domain.exception.MenuHasChildrenException;
import com.tengan.mall.admin.domain.exception.UnsupportedFileTypeException;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class AdminExceptionHandler {

    /**
     * BFF 呼叫 tengan-product 收到的非 2xx 回應，原樣轉發狀態碼+訊息——不重新翻譯成 tengan-admin
     * 自己的例外語意，因為業務規則的真相在 tengan-product（見 ProductCategoryPort 的說明）。
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<byte[]> handleDownstreamProductError(RestClientResponseException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
    }

    @ExceptionHandler({DuplicateAdminUsernameException.class, DuplicateRoleCodeException.class,
            MenuHasChildrenException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({InvalidAdminCredentialsException.class, AdminAccountDisabledException.class,
            InvalidAdminRefreshTokenException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorized(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({InvalidMenuRouteException.class, InvalidMenuHierarchyException.class,
            UnsupportedFileTypeException.class, FileTooLargeException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}

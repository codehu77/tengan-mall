package com.tengan.mall.auth.interfaces.rest;

import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.exception.InvalidCredentialsException;
import com.tengan.mall.auth.domain.exception.InvalidOrExpiredVerificationTokenException;
import com.tengan.mall.auth.domain.exception.InvalidOtpCodeException;
import com.tengan.mall.auth.domain.exception.InvalidRefreshTokenException;
import com.tengan.mall.auth.domain.exception.OtpCooldownException;
import com.tengan.mall.auth.domain.exception.TooManyLoginAttemptsException;
import com.tengan.mall.auth.domain.exception.TooManyOtpAttemptsException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對——跟 tengan-product 同一種處理方式。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFound(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IdentifierAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(IdentifierAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidOtpCodeException.class,
            InvalidRefreshTokenException.class, InvalidOrExpiredVerificationTokenException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorized(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({OtpCooldownException.class, TooManyOtpAttemptsException.class,
            TooManyLoginAttemptsException.class})
    public ResponseEntity<Map<String, String>> handleTooManyRequests(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", e.getMessage()));
    }
}

package com.tengan.mall.auth.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String reason) {
        super("refresh token 無效: " + reason);
    }
}

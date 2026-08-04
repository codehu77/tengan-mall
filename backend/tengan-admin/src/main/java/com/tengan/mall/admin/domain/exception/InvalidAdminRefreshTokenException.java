package com.tengan.mall.admin.domain.exception;

public class InvalidAdminRefreshTokenException extends RuntimeException {

    public InvalidAdminRefreshTokenException(String reason) {
        super("refresh token 無效: " + reason);
    }
}

package com.tengan.mall.auth.domain.exception;

public class InvalidOAuthTokenException extends RuntimeException {

    public InvalidOAuthTokenException(String reason) {
        super("第三方登入憑證無效: " + reason);
    }
}

package com.tengan.mall.auth.domain.exception;

public class InvalidOrExpiredVerificationTokenException extends RuntimeException {

    public InvalidOrExpiredVerificationTokenException() {
        super("驗證憑證無效或已過期，請重新驗證");
    }
}

package com.tengan.mall.auth.domain.exception;

public class TooManyLoginAttemptsException extends RuntimeException {

    public TooManyLoginAttemptsException() {
        super("登入失敗次數過多，請稍後再試");
    }
}

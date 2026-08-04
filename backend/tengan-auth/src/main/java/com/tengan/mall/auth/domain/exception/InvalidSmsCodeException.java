package com.tengan.mall.auth.domain.exception;

public class InvalidSmsCodeException extends RuntimeException {

    public InvalidSmsCodeException() {
        super("簡訊驗證碼錯誤或已過期");
    }
}

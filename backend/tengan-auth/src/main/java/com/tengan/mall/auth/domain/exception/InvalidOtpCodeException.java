package com.tengan.mall.auth.domain.exception;

public class InvalidOtpCodeException extends RuntimeException {

    public InvalidOtpCodeException() {
        super("驗證碼錯誤或已過期");
    }
}

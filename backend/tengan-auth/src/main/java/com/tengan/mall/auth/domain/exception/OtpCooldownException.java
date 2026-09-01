package com.tengan.mall.auth.domain.exception;

public class OtpCooldownException extends RuntimeException {

    public OtpCooldownException(String identifier) {
        super("驗證碼發送冷卻中，請稍後再試: " + identifier);
    }
}

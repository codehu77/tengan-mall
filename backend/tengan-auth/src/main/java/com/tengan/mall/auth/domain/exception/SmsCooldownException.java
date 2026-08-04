package com.tengan.mall.auth.domain.exception;

public class SmsCooldownException extends RuntimeException {

    public SmsCooldownException(String phone) {
        super("簡訊發送冷卻中，請稍後再試: " + phone);
    }
}

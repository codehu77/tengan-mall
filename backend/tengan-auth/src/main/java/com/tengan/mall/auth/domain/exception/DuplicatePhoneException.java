package com.tengan.mall.auth.domain.exception;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String phone) {
        super("phone 已被註冊: " + phone);
    }
}

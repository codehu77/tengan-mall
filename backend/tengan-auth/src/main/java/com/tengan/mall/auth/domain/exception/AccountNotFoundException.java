package com.tengan.mall.auth.domain.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long id) {
        super("帳號不存在: " + id);
    }
}

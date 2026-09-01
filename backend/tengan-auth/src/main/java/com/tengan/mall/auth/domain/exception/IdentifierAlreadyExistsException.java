package com.tengan.mall.auth.domain.exception;

public class IdentifierAlreadyExistsException extends RuntimeException {

    public IdentifierAlreadyExistsException(String identifier) {
        super("此手機或 Email 已被註冊，請改用登入: " + identifier);
    }
}

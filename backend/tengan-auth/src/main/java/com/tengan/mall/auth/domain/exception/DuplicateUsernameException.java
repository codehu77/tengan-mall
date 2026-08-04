package com.tengan.mall.auth.domain.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("username 已被註冊: " + username);
    }
}

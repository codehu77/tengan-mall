package com.tengan.mall.admin.domain.exception;

public class DuplicateAdminUsernameException extends RuntimeException {

    public DuplicateAdminUsernameException(String username) {
        super("username 已被使用: " + username);
    }
}

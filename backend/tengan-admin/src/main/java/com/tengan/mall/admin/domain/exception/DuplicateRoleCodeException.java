package com.tengan.mall.admin.domain.exception;

public class DuplicateRoleCodeException extends RuntimeException {

    public DuplicateRoleCodeException(String roleCode) {
        super("roleCode 已被使用: " + roleCode);
    }
}

package com.tengan.mall.admin.domain.exception;

public class InvalidAdminCredentialsException extends RuntimeException {

    public InvalidAdminCredentialsException() {
        super("帳號或密碼錯誤");
    }
}

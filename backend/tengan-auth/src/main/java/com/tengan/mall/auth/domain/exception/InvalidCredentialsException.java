package com.tengan.mall.auth.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("帳號或密碼錯誤");
    }
}

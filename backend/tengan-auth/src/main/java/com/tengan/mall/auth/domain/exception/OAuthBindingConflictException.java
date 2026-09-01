package com.tengan.mall.auth.domain.exception;

/** 涵蓋「這個第三方帳號已綁定別人」跟「自己已經綁過這個 provider」兩種情境，訊息由呼叫端決定。 */
public class OAuthBindingConflictException extends RuntimeException {

    public OAuthBindingConflictException(String message) {
        super(message);
    }
}

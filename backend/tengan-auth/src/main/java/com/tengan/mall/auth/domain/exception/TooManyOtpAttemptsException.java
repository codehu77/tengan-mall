package com.tengan.mall.auth.domain.exception;

public class TooManyOtpAttemptsException extends RuntimeException {

    public TooManyOtpAttemptsException() {
        super("驗證碼輸入錯誤次數過多，請重新發送");
    }
}

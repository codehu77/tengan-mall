package com.tengan.mall.seckill.domain.exception;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(Long sessionId) {
        super("找不到秒殺場次範本: sessionId=" + sessionId);
    }
}

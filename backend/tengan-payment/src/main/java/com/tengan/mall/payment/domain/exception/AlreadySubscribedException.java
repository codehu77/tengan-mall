package com.tengan.mall.payment.domain.exception;

public class AlreadySubscribedException extends RuntimeException {

    public AlreadySubscribedException(Long memberId) {
        super("已經有一份進行中的訂閱: memberId=" + memberId);
    }
}

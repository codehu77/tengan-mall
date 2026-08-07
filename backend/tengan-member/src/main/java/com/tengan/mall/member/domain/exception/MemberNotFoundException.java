package com.tengan.mall.member.domain.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long id) {
        super("會員不存在: " + id);
    }
}

package com.tengan.mall.member.domain.exception;

public class MemberAddressNotFoundException extends RuntimeException {

    public MemberAddressNotFoundException(Long id) {
        super("收件地址不存在: " + id);
    }
}

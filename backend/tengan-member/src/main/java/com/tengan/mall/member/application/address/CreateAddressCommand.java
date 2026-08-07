package com.tengan.mall.member.application.address;

public record CreateAddressCommand(Long memberId, String receiverName, String receiverPhone, String address,
        boolean isDefault) {
}

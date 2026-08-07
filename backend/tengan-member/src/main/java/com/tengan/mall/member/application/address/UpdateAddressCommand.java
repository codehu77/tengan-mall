package com.tengan.mall.member.application.address;

public record UpdateAddressCommand(Long memberId, Long addressId, String receiverName, String receiverPhone,
        String address) {
}

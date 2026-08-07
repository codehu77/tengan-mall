package com.tengan.mall.member.interfaces.rest.dto;

public record AddressResponse(Long id, String receiverName, String receiverPhone, String address,
        boolean isDefault) {
}

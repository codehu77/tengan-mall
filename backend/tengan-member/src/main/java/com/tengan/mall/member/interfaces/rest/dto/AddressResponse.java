package com.tengan.mall.member.interfaces.rest.dto;

public record AddressResponse(Long id, String receiverName, String receiverPhone, String city, String district,
        String postalCode, String street, boolean isDefault) {
}

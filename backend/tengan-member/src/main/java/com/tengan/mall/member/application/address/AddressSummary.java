package com.tengan.mall.member.application.address;

public record AddressSummary(Long id, String receiverName, String receiverPhone, String city, String district,
        String postalCode, String street, boolean isDefault) {
}

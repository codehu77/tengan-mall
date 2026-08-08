package com.tengan.mall.member.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAddressRequest(@NotBlank String receiverName, @NotBlank String receiverPhone,
        @NotBlank String city, @NotBlank String district, @NotBlank String postalCode, @NotBlank String street) {
}

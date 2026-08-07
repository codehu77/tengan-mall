package com.tengan.mall.member.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(@NotBlank String receiverName, @NotBlank String receiverPhone,
        @NotBlank String address, boolean isDefault) {
}

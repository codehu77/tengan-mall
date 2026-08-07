package com.tengan.mall.member.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAddressRequest(@NotBlank String receiverName, @NotBlank String receiverPhone,
        @NotBlank String address) {
}

package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePhoneVerifyRequest(@NotBlank String newPhone, @NotBlank String code) {
}

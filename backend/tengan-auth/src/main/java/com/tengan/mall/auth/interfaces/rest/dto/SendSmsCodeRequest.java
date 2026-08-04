package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record SendSmsCodeRequest(@NotBlank String phone, @NotBlank String purpose) {
}

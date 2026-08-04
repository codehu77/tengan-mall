package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifySmsCodeRequest(@NotBlank String phone, @NotBlank String purpose, @NotBlank String code) {
}

package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordVerifyRequest(@NotBlank String identifier, @NotBlank String code) {
}

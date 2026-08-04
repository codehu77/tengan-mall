package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String phone,
        @NotBlank String password,
        @NotBlank String code) {
}

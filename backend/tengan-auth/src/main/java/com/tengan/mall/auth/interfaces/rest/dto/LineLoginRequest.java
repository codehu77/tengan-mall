package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LineLoginRequest(@NotBlank String code, @NotBlank String nonce) {
}

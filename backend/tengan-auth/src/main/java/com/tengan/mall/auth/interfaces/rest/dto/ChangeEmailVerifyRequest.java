package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeEmailVerifyRequest(@NotBlank String newEmail, @NotBlank String code) {
}

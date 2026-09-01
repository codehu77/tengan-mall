package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterVerifyRequest(@NotBlank String identifier, @NotBlank String code) {
}

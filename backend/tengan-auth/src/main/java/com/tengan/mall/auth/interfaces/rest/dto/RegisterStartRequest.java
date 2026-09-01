package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterStartRequest(@NotBlank String identifier) {
}

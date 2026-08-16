package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTierRequest(@NotBlank String tier, @NotBlank String reason) {
}

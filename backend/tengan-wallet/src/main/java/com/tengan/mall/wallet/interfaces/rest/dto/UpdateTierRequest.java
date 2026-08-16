package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTierRequest(@NotBlank String tier, @NotBlank String reason) {
}

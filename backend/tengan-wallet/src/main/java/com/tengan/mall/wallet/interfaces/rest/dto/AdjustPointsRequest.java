package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustPointsRequest(@NotNull Long memberId, @NotNull Integer points, @NotBlank String reason) {
}

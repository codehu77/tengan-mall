package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustPointsRequest(@NotNull Integer points, @NotBlank String reason) {
}

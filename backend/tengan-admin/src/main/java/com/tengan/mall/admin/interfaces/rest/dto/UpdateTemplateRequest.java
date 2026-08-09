package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record UpdateTemplateRequest(@NotBlank String name, @NotNull BigDecimal thresholdAmount,
        @NotNull BigDecimal discountAmount, @Min(1) int totalCount, @NotNull Instant effectiveStart,
        @NotNull Instant effectiveEnd) {
}

package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** delta 可正可負（+10=補貨、-3=報損），不是覆蓋絕對值。 */
public record AdjustStockRequest(@NotNull Long wareId, int delta, @NotBlank String reason) {
}

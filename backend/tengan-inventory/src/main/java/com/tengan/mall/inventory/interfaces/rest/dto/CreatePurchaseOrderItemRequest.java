package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreatePurchaseOrderItemRequest(@NotNull Long skuId, @Min(1) int orderedQty) {
}

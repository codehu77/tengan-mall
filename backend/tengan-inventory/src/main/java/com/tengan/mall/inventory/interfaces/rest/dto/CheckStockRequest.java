package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CheckStockRequest(@NotEmpty @Valid List<CheckStockItemRequest> items) {
}

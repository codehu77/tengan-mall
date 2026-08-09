package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LockInventoryRequest(@NotBlank String orderSn, @NotEmpty @Valid List<LockInventoryItemRequest> items) {
}

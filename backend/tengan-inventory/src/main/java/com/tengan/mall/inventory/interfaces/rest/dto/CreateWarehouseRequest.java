package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWarehouseRequest(@NotBlank String name, @NotBlank String address) {
}

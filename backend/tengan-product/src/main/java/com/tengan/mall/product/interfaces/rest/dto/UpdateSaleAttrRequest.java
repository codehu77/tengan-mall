package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSaleAttrRequest(@NotBlank String name, String unit, boolean searchable, int sort) {
}

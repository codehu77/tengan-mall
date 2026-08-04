package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSaleAttrRequest(@NotBlank String name, boolean searchable, int sort) {
}

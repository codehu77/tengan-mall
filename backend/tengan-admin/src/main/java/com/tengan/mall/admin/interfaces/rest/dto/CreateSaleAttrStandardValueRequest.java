package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSaleAttrStandardValueRequest(@NotBlank String label, int sort) {
}

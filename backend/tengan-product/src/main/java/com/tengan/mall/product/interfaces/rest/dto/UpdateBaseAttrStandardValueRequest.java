package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBaseAttrStandardValueRequest(@NotBlank String label, boolean enabled, int sort) {
}

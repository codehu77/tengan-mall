package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBaseAttrStandardValueRequest(@NotBlank String label, int sort) {
}

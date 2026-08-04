package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(@NotBlank String name, String icon, int sort) {
}

package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(Long parentId, @NotBlank String name, String icon, int sort) {
}

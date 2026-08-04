package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBaseAttrGroupRequest(@NotNull Long categoryId, @NotBlank String name, int sort) {
}

package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBaseAttrRequest(@NotNull Long categoryId, @NotNull Long attrGroupId, @NotBlank String name,
        String unit, boolean searchable, int sort) {
}

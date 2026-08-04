package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBaseAttrRequest(@NotNull Long categoryId, @NotNull Long attrGroupId, @NotBlank String name,
        boolean searchable, int sort) {
}

package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateBaseAttrRequest(@NotNull Long attrGroupId, @NotBlank String name, boolean searchable,
        int sort) {
}

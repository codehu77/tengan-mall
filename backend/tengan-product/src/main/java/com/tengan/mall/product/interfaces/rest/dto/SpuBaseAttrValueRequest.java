package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SpuBaseAttrValueRequest(@NotNull Long attrId, @NotBlank String attrValue) {
}

package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SkuSaleAttrValueRequest(@NotNull Long attrId, String attrValue) {
}

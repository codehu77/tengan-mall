package com.tengan.mall.product.application.baseattr;

public record BaseAttrSummary(Long id, Long categoryId, Long attrGroupId, String name, boolean searchable,
        int sort) {
}

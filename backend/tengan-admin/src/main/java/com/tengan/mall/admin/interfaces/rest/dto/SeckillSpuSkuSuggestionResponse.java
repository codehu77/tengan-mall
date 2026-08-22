package com.tengan.mall.admin.interfaces.rest.dto;

public record SeckillSpuSkuSuggestionResponse(Long skuId, String variantLabel, String mainImage, int realStock,
        int suggestedQuota) {
}

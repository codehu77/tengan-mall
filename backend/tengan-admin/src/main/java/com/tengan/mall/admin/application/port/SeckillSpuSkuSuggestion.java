package com.tengan.mall.admin.application.port;

public record SeckillSpuSkuSuggestion(Long skuId, String variantLabel, String mainImage, int realStock,
        int suggestedQuota) {
}

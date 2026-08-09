package com.tengan.mall.admin.interfaces.rest.dto;

public record SkuStockItemResponse(Long wareId, Long skuId, int stock, int lockedStock, Long spuId, String skuName,
        String mainImage) {
}

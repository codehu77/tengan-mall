package com.tengan.mall.admin.application.port;

public record SkuStockItem(Long wareId, Long skuId, int stock, int lockedStock) {
}

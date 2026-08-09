package com.tengan.mall.inventory.application.stock;

public record WareSkuQueryItem(Long wareId, Long skuId, int stock, int lockedStock) {
}

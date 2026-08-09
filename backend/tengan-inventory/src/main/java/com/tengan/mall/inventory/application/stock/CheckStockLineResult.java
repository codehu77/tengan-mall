package com.tengan.mall.inventory.application.stock;

public record CheckStockLineResult(Long skuId, boolean sufficient, int availableStock) {
}

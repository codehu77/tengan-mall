package com.tengan.mall.inventory.interfaces.rest.dto;

public record WareSkuStockResponse(Long wareId, Long skuId, int stock, int lockedStock) {
}

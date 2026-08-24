package com.tengan.mall.inventory.application.stock;

public interface ListSkuStockUseCase {

    ListSkuStockResult list(Long wareId, Long skuIdKeyword, boolean onlyLowStock, int pageNum, int pageSize);
}

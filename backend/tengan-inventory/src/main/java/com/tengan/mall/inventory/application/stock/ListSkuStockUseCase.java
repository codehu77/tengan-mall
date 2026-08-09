package com.tengan.mall.inventory.application.stock;

public interface ListSkuStockUseCase {

    ListSkuStockResult list(Long wareId, Long skuIdKeyword, int pageNum, int pageSize);
}

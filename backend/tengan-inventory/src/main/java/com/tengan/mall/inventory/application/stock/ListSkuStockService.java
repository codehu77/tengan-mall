package com.tengan.mall.inventory.application.stock;

import org.springframework.stereotype.Service;

@Service
public class ListSkuStockService implements ListSkuStockUseCase {

    private final WareSkuQueryPort wareSkuQueryPort;

    public ListSkuStockService(WareSkuQueryPort wareSkuQueryPort) {
        this.wareSkuQueryPort = wareSkuQueryPort;
    }

    @Override
    public ListSkuStockResult list(Long wareId, Long skuIdKeyword, boolean onlyLowStock, int pageNum, int pageSize) {
        var items = wareSkuQueryPort.search(wareId, skuIdKeyword, onlyLowStock, pageNum, pageSize);
        long total = wareSkuQueryPort.countSearch(wareId, skuIdKeyword, onlyLowStock);
        return new ListSkuStockResult(items, total);
    }
}

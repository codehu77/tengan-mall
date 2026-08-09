package com.tengan.mall.inventory.application.stock;

import java.util.List;

public record ListSkuStockResult(List<WareSkuQueryItem> items, long total) {
}

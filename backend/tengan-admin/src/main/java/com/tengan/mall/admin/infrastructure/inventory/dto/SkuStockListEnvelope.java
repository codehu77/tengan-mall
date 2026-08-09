package com.tengan.mall.admin.infrastructure.inventory.dto;

import com.tengan.mall.admin.application.port.SkuStockItem;
import java.util.List;

public record SkuStockListEnvelope(List<SkuStockItem> items, long total) {
}

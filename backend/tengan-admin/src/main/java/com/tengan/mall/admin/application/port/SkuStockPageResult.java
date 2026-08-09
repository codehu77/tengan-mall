package com.tengan.mall.admin.application.port;

import java.util.List;

public record SkuStockPageResult(List<SkuStockItem> items, long total) {
}

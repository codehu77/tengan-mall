package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record ListSkuStockResponse(List<SkuStockItemResponse> items, long total) {
}

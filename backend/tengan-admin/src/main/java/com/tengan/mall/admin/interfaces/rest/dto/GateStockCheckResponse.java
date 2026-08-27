package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

/** items 是目前庫存 <=0 的 SKU 清單，空清單代表這顆 SPU 底下所有 SKU 都有庫存。 */
public record GateStockCheckResponse(List<GateStockCheckItemResponse> items) {
}

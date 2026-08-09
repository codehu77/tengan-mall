package com.tengan.mall.admin.application.port;

/** delta 可正可負（+10=補貨、-3=報損），不是覆蓋絕對值。 */
public record AdjustStockPayload(Long wareId, int delta, String reason) {
}

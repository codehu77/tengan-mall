package com.tengan.mall.inventory.application.stock;

/** delta 可正可負（+10=補貨、-3=報損），不是覆蓋絕對值——見 WareSkuRepository#adjustStockDelta 的 javadoc。 */
public record AdjustStockCommand(String operator, Long wareId, Long skuId, int delta, String reason) {
}

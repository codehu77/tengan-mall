package com.tengan.mall.order.domain.model;

import java.math.BigDecimal;

/**
 * Order 底下的明細列，建立後不可變、沒有獨立生命週期（比照 WareOrderTaskDetail/PurchaseOrderItem
 * 用 record，不需要自己的 Repository）。skuName/skuImage/price 是下單當下的快照，不是即時查詢
 * （見 db_design_conventions「快照 vs 即時查詢判斷準則」：order_item 是一旦成交的歷史憑證）。
 */
public record OrderItem(Long id, Long skuId, Long spuId, String skuName, String skuImage, BigDecimal price,
        int count, BigDecimal subtotal) {
}

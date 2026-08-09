package com.tengan.mall.inventory.domain.model;

/**
 * PurchaseOrder 底下的明細列，沒有自己的生命週期意義（比照 WareOrderTaskDetail 用 record）。
 * receivedQty 收貨前為 null，收貨時才由 markReceived 一併寫入。
 */
public record PurchaseOrderItem(Long id, Long skuId, int orderedQty, Integer receivedQty) {
}

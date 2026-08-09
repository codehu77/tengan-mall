package com.tengan.mall.inventory.application.purchaseorder;

public record PurchaseOrderItemResult(Long id, Long skuId, int orderedQty, Integer receivedQty) {
}

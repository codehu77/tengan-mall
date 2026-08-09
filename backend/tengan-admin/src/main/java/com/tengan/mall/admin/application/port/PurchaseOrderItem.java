package com.tengan.mall.admin.application.port;

public record PurchaseOrderItem(Long id, Long skuId, int orderedQty, Integer receivedQty) {
}

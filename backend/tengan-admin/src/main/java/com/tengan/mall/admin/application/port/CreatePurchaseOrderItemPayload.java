package com.tengan.mall.admin.application.port;

public record CreatePurchaseOrderItemPayload(Long skuId, int orderedQty) {
}

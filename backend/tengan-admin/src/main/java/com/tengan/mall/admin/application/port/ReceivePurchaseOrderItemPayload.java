package com.tengan.mall.admin.application.port;

public record ReceivePurchaseOrderItemPayload(Long itemId, int receivedQty) {
}

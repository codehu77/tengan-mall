package com.tengan.mall.inventory.interfaces.rest.dto;

public record PurchaseOrderItemResponse(Long id, Long skuId, int orderedQty, Integer receivedQty) {
}

package com.tengan.mall.admin.interfaces.rest.dto;

public record PurchaseOrderItemResponse(Long id, Long skuId, int orderedQty, Integer receivedQty) {
}

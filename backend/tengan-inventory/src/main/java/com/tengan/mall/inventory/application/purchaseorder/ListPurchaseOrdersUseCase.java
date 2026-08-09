package com.tengan.mall.inventory.application.purchaseorder;

public interface ListPurchaseOrdersUseCase {

    ListPurchaseOrdersResult list(Integer status, Long wareId, int pageNum, int pageSize);
}

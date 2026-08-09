package com.tengan.mall.inventory.application.purchaseorder;

import org.springframework.stereotype.Service;

@Service
public class ListPurchaseOrdersService implements ListPurchaseOrdersUseCase {

    private final PurchaseOrderQueryPort purchaseOrderQueryPort;

    public ListPurchaseOrdersService(PurchaseOrderQueryPort purchaseOrderQueryPort) {
        this.purchaseOrderQueryPort = purchaseOrderQueryPort;
    }

    @Override
    public ListPurchaseOrdersResult list(Integer status, Long wareId, int pageNum, int pageSize) {
        var items = purchaseOrderQueryPort.search(status, wareId, pageNum, pageSize);
        long total = purchaseOrderQueryPort.countSearch(status, wareId);
        return new ListPurchaseOrdersResult(items, total);
    }
}

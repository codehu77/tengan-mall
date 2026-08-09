package com.tengan.mall.admin.application.port;

/** 呼叫 tengan-inventory 的採購單 internal 端點，跟 {@link InventoryStockPort} 同樣的純代理原則。 */
public interface PurchaseOrderPort {

    Long createPurchaseOrder(CreatePurchaseOrderPayload payload, String operatorToken);

    PurchaseOrderPageResult listPurchaseOrders(Integer status, Long wareId, int page, int pageSize);

    PurchaseOrderDetail getPurchaseOrderDetail(Long id);

    void receivePurchaseOrder(Long id, ReceivePurchaseOrderPayload payload, String operatorToken);
}

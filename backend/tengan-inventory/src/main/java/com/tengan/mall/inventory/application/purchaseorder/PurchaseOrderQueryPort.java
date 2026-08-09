package com.tengan.mall.inventory.application.purchaseorder;

import java.util.List;

/** 採購單列表跟「待收貨清單」共用同一支查詢，差別只在前端預設 status 篩選值。 */
public interface PurchaseOrderQueryPort {

    List<PurchaseOrderSummary> search(Integer status, Long wareId, int pageNum, int pageSize);

    long countSearch(Integer status, Long wareId);
}

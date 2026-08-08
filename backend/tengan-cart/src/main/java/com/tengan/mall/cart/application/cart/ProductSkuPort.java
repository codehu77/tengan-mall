package com.tengan.mall.cart.application.cart;

import java.util.List;

/**
 * 購物車不存價格快照，讀取時一律即時呼叫 tengan-product 查目前價格/名稱/圖片（見
 * cart_storage_decision）。實作打 tengan-product 新增的 internal 批次端點
 * （/internal/products/skus?ids=...，不套上架過濾），已下架/已刪除的 sku 不會出現在回傳結果，
 * 呼叫端據此判斷該項目目前不可購買。
 */
public interface ProductSkuPort {

    List<ProductSkuInfo> findByIds(List<Long> skuIds);
}

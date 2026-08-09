package com.tengan.mall.admin.application.port;

import java.util.List;

/**
 * 呼叫 tengan-product 的 SKU 批次查詢端點，供庫存列表用 skuId 反查商品名稱/圖片顯示——
 * tengan-inventory 完全不知道商品資訊，只能靠 tengan-admin 這層批次查 tengan-product 補齊。
 */
public interface ProductSkuPort {

    List<SkuItem> batchGet(List<Long> skuIds);
}

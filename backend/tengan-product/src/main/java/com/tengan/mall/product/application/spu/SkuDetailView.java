package com.tengan.mall.product.application.spu;

import java.math.BigDecimal;
import java.util.List;

/**
 * 兩種消費者共用的攤平投影：一是內部/公開 Spu 詳情內嵌的 sku 清單（走 SpuRepository 聚合根），
 * 二是 GetPublicSkuDetailUseCase 這種前台高頻單筆查詢（走 SkuDetailPort，不經過 Spu 聚合根，見該
 * Port 的註解）。形狀相同所以共用同一個 View 型別，不重複定義。
 */
public record SkuDetailView(Long id, Long spuId, String name, BigDecimal price, String mainImage, int saleCount,
        int sort, List<SkuImageView> images, List<SkuSaleAttrValueView> saleAttrValues) {
}

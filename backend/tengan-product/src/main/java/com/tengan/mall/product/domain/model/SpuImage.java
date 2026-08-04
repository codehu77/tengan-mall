package com.tengan.mall.product.domain.model;

/**
 * Spu 層級共通圖片——所有底下的 Sku 共用（例如整體外觀/包裝/尺寸表），跟 Sku 各自專屬的
 * {@link SkuImage} 分開存。沒有獨立生命週期意義，Repository 用 delete-and-reinsert 持久化。
 */
public record SpuImage(String imageUrl, int sort) {
}

package com.tengan.mall.product.domain.model;

/** Sku 底下的圖片。沒有獨立生命週期意義，Repository 用 delete-and-reinsert 持久化，不像 Sku 本身需要 diff。 */
public record SkuImage(String imageUrl, int sort) {
}

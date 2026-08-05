package com.tengan.mall.search.application;

/** 呼叫端命名——供全量重建索引拉取 tengan-product 的搜尋匯出端點，只回 ON_SHELF 商品。 */
public interface ProductCatalogPort {

    ProductCatalogPage fetchPage(int pageNum, int pageSize);
}

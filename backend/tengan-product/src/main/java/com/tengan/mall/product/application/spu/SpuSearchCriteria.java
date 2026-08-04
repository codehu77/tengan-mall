package com.tengan.mall.product.application.spu;

/** 查詢用參數物件，純粹收斂 {@link SpuSearchPort#search} 的參數數量，沒有任何不變條件。 */
public record SpuSearchCriteria(Long categoryId, Long brandId, String name, Integer status) {
}

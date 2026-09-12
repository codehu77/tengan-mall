package com.tengan.mall.search.application;

import java.math.BigDecimal;
import java.util.List;

/** ProductCatalogSpuItem.skus() 底下的單一規格變體，形狀對應 tengan-product 的 SkuVariantResponse。 */
public record ProductCatalogSkuVariantItem(Long skuId, String skuName, BigDecimal price, String mainImage,
        int saleCount, List<ProductCatalogAttrItem> saleAttrs) {
}

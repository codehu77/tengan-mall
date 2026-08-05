package com.tengan.mall.search.application;

import java.math.BigDecimal;
import java.util.List;

/** ProductCatalogPort 的回應形狀——欄位對應 tengan-product 的 SkuSearchDocumentResponse，只靠 JSON 欄位名稱對齊。 */
public record ProductCatalogSkuItem(Long skuId, Long spuId, String skuName, String spuName, BigDecimal price,
        String mainImage, int saleCount, Long brandId, String brandName, Long catalog1Id, String catalog1Name,
        Long catalog2Id, String catalog2Name, Long catalog3Id, String catalog3Name,
        List<ProductCatalogAttrItem> attrs) {
}

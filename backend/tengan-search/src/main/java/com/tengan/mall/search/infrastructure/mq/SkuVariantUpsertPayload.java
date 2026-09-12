package com.tengan.mall.search.infrastructure.mq;

import java.math.BigDecimal;
import java.util.List;

/** SpuUpsertPayload.skus() 底下的單一規格變體，欄位形狀對應 tengan-product 的 SkuVariantPayload。 */
public record SkuVariantUpsertPayload(Long skuId, String skuName, BigDecimal price, String mainImage, int saleCount,
        List<SkuAttrPayload> saleAttrs) {
}

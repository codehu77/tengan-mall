package com.tengan.mall.product.application.spu;

import java.math.BigDecimal;
import java.util.List;

/** 一個 SPU 底下單一規格變體——巢狀掛在 SpuSearchDocumentPayload.skus() 裡。 */
public record SkuVariantPayload(Long skuId, String skuName, BigDecimal price, String mainImage, int saleCount,
        List<SearchAttrPayload> saleAttrs) {
}

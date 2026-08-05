package com.tengan.mall.search.infrastructure.mq;

import java.math.BigDecimal;
import java.util.List;

/** 欄位形狀跟 tengan-product 的 SkuSearchDocumentPayload 對應——兩邊只靠 JSON 欄位名稱對齊，不共用型別。 */
public record SkuUpsertPayload(Long skuId, Long spuId, String skuName, String spuName, BigDecimal price,
        String mainImage, int saleCount, Long brandId, String brandName, Long catalog1Id, String catalog1Name,
        Long catalog2Id, String catalog2Name, Long catalog3Id, String catalog3Name, List<SkuAttrPayload> attrs) {
}

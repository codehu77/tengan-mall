package com.tengan.mall.product.application.spu;

import java.math.BigDecimal;
import java.util.List;

/**
 * 「厚事件」的核心資料形狀——tengan-product 組好完整、已攤平的搜尋文件（分類祖先鏈、品牌名稱、
 * 只含 searchable=true 的屬性值），tengan-search 收到後直接 upsert 進 ES，不用回頭查任何其他服務。
 *
 * <p>一個 SPU 一筆文件——minPrice/maxPrice 是索引階段就算好的彙總值，不同規格（skus）各自的價格
 * 明細掛在 skus 陣列裡供規格層級篩選使用，列表卡片只看 minPrice/maxPrice。</p>
 */
public record SpuSearchDocumentPayload(Long spuId, String spuName, String spuMainImage, BigDecimal minPrice,
        BigDecimal maxPrice, int saleCount, Long brandId, String brandName, Long catalog1Id, String catalog1Name,
        Long catalog2Id, String catalog2Name, Long catalog3Id, String catalog3Name,
        List<SearchAttrPayload> baseAttrs, List<SkuVariantPayload> skus) {
}

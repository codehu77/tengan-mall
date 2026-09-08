package com.tengan.mall.devtools.client.dto;

import java.time.LocalDateTime;
import java.util.List;

/** 對應 tengan-product 的 CreateSpuRequest；即將開賣排程欄位這裡一律留 null/false。 */
public record CreateSpuRequest(Long categoryId, Long brandId, String name, String description, String mainImage,
        LocalDateTime saleStartTime, boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt,
        List<SpuBaseAttrValue> attrValues, List<SpuImage> images, List<SkuDraft> skus) {
}

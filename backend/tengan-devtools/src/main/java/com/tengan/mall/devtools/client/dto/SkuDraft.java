package com.tengan.mall.devtools.client.dto;

import java.math.BigDecimal;
import java.util.List;

/** id 恆為 null——這個工具只做「建立」，不做編輯既有 SPU（見 tengan-product SkuIdMismatchException：
 * create 流程帶非 null 的 sku id 一定會被拒絕）。 */
public record SkuDraft(String name, BigDecimal price, String mainImage, int sort, List<SkuImage> images,
        List<SkuSaleAttrValue> saleAttrValues, Integer purchaseLimitPerUser) {
}

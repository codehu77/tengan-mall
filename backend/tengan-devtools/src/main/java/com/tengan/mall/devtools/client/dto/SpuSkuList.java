package com.tengan.mall.devtools.client.dto;

import java.util.List;

/** 對應 tengan-product SpuDetailResponse，只取建庫存用得到的欄位（SPU 底下每顆 SKU 的 id）。 */
public record SpuSkuList(Long id, List<SkuRef> skus) {
}

package com.tengan.mall.admin.application.port;

import java.util.List;

public record SpuDetailItem(Long id, Long categoryId, Long brandId, String name, String description,
        String mainImage, int status, List<SpuBaseAttrValueItem> attrValues, List<SpuImageItem> images,
        List<SkuItem> skus) {
}

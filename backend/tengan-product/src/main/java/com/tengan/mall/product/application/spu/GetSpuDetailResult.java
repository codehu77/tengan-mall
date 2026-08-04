package com.tengan.mall.product.application.spu;

import java.util.List;

public record GetSpuDetailResult(Long id, Long categoryId, Long brandId, String name, String description,
        String mainImage, int status, List<SpuBaseAttrValueView> attrValues, List<SpuImageView> images,
        List<SkuDetailView> skus) {
}

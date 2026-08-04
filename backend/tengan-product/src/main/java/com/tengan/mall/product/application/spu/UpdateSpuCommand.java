package com.tengan.mall.product.application.spu;

import java.util.List;

public record UpdateSpuCommand(Long spuId, Long categoryId, Long brandId, String name, String description,
        String mainImage, List<SpuBaseAttrValueCommand> attrValues, List<SpuImageCommand> images,
        List<SkuCommand> skus) {
}

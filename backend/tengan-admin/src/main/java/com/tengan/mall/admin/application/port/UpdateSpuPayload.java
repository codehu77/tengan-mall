package com.tengan.mall.admin.application.port;

import java.util.List;

/** 整批替換語意：skus/attrValues 帶的是更新後的完整集合，不是差異片段。 */
public record UpdateSpuPayload(Long categoryId, Long brandId, String name, String description, String mainImage,
        List<SpuBaseAttrValuePayload> attrValues, List<SpuImagePayload> images, List<SkuPayload> skus) {
}

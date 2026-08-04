package com.tengan.mall.admin.application.port;

import java.util.List;

/** skus 可以是空 list——建立時先不填也合法，見 tengan-product 的 CreateSpuRequest 容忍度。 */
public record CreateSpuPayload(Long categoryId, Long brandId, String name, String description, String mainImage,
        List<SpuBaseAttrValuePayload> attrValues, List<SpuImagePayload> images, List<SkuPayload> skus) {
}

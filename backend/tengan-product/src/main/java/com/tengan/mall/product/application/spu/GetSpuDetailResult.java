package com.tengan.mall.product.application.spu;

import java.time.LocalDateTime;
import java.util.List;

public record GetSpuDetailResult(Long id, Long categoryId, Long catalog1Id, Long brandId, String name,
        String description, String mainImage, int status, LocalDateTime saleStartTime, boolean trafficGateEnabled,
        LocalDateTime gateCloseTime, boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt,
        List<SpuBaseAttrValueView> attrValues, List<SpuImageView> images, List<SkuDetailView> skus) {
}

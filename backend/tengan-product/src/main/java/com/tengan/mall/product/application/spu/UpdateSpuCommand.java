package com.tengan.mall.product.application.spu;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateSpuCommand(Long spuId, Long categoryId, Long brandId, String name, String description,
        String mainImage, LocalDateTime saleStartTime, boolean trafficGateEnabled, LocalDateTime gateCloseTime,
        boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt, List<SpuBaseAttrValueCommand> attrValues,
        List<SpuImageCommand> images, List<SkuCommand> skus) {
}

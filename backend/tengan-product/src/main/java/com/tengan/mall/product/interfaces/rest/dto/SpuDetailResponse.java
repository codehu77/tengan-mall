package com.tengan.mall.product.interfaces.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SpuDetailResponse(Long id, Long categoryId, Long catalog1Id, Long brandId, String name,
        String description, String mainImage, int status, LocalDateTime saleStartTime, boolean trafficGateEnabled,
        LocalDateTime gateCloseTime, boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt,
        List<SpuBaseAttrValueResponse> attrValues, List<SpuImageResponse> images, List<SkuDetailResponse> skus) {
}

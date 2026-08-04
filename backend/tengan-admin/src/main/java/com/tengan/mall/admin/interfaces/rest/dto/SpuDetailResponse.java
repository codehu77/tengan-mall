package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record SpuDetailResponse(Long id, Long categoryId, Long brandId, String name, String description,
        String mainImage, int status, List<SpuBaseAttrValueResponse> attrValues, List<SpuImageResponse> images,
        List<SkuDetailResponse> skus) {
}

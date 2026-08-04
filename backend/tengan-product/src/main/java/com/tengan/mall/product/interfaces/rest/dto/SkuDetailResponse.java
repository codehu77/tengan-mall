package com.tengan.mall.product.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record SkuDetailResponse(Long id, Long spuId, String name, BigDecimal price, String mainImage, int saleCount,
        int sort, List<SkuImageResponse> images, List<SkuSaleAttrValueResponse> saleAttrValues) {
}

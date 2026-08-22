package com.tengan.mall.seckill.interfaces.rest.dto;

import java.util.List;

public record PublicProductResponse(Long spuId, String name, String mainImage, List<PublicSkuResponse> skus) {
}

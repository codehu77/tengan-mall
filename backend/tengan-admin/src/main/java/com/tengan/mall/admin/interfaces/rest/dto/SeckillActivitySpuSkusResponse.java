package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record SeckillActivitySpuSkusResponse(Long spuId, String spuName, String spuMainImage,
        BigDecimal seckillPrice, int limitPerUser, List<SeckillSpuSkuSuggestionResponse> items) {
}

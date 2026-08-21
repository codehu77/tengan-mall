package com.tengan.mall.seckill.interfaces.rest.dto;

import java.math.BigDecimal;

public record PublicSkuResponse(Long skuId, Long spuId, String name, String mainImage, BigDecimal originalPrice,
        BigDecimal seckillPrice, int limitPerUser, int remaining) {
}

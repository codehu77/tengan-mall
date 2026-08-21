package com.tengan.mall.seckill.application.display;

import java.math.BigDecimal;

public record ActiveSkuView(Long skuId, Long spuId, String name, String mainImage, BigDecimal originalPrice,
        BigDecimal seckillPrice, int limitPerUser, int remaining) {
}

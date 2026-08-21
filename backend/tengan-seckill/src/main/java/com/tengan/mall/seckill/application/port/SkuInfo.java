package com.tengan.mall.seckill.application.port;

import java.math.BigDecimal;

public record SkuInfo(Long skuId, Long spuId, String name, String mainImage, BigDecimal price) {
}

package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;

public record OrderItemDetail(Long skuId, Long spuId, String skuName, String skuImage, BigDecimal price, int count,
        BigDecimal subtotal) {
}

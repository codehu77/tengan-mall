package com.tengan.mall.order.application.order;

import java.math.BigDecimal;

public record OrderItemView(Long skuId, Long spuId, String skuName, String skuImage, BigDecimal price, int count,
        BigDecimal subtotal) {
}

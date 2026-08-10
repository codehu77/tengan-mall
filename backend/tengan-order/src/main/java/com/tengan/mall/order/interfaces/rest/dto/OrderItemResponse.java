package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long skuId, Long spuId, String skuName, String skuImage, BigDecimal price,
        int count, BigDecimal subtotal) {
}

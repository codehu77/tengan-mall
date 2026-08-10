package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;

public record ConfirmedItemResponse(Long skuId, Long spuId, String name, String mainImage, BigDecimal price,
        int count, BigDecimal subtotal) {
}

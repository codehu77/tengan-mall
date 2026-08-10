package com.tengan.mall.order.application.order;

import java.math.BigDecimal;

public record ConfirmedItemView(Long skuId, Long spuId, String name, String mainImage, BigDecimal price, int count,
        BigDecimal subtotal) {
}

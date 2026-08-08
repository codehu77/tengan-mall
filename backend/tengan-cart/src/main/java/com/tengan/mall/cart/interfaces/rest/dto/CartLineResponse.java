package com.tengan.mall.cart.interfaces.rest.dto;

import java.math.BigDecimal;

public record CartLineResponse(Long itemId, Long skuId, Long spuId, String name, BigDecimal price, String mainImage,
        int count, boolean checked, String specText, boolean available) {
}

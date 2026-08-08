package com.tengan.mall.cart.application.cart;

import java.math.BigDecimal;

/** 即時查價結果的攤平投影——不落地儲存，每次讀取購物車都重新向 tengan-product 查詢。 */
public record ProductSkuInfo(Long skuId, Long spuId, String name, BigDecimal price, String mainImage) {
}

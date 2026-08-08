package com.tengan.mall.cart.application.cart;

/** 給 tengan-order 用（internal 端點）：結帳只需要 skuId+count，不需要即時查價（訂單服務自己會再核價）。 */
public record CheckedItemView(Long skuId, int count) {
}

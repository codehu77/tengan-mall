package com.tengan.mall.order.application.port;

/** tengan-cart /internal/cart/{userId}/items 回應的扁平投影（見 GET /orders/confirm 開發細節）。 */
public record CheckedCartItem(Long skuId, int count) {
}

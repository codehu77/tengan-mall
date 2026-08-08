package com.tengan.mall.cart.application.cart;

/** 訪客購物車項目：純資料結構，沒有業務規則要保護，不比照 CartItem 做成聚合根（見 cart_storage_decision）。 */
public record GuestCartItem(Long skuId, int count, boolean checked, String specText) {
}

package com.tengan.mall.cart.application.cart;

/** 加入購物車前檢查這顆 SKU 目前是否可購買（sale_start_time 是否已到）。 */
public interface InventoryAvailabilityPort {

    boolean isPurchasable(Long skuId);
}

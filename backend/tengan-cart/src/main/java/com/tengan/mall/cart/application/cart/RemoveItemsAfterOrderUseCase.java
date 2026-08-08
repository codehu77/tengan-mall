package com.tengan.mall.cart.application.cart;

import java.util.List;

/** 給 tengan-order 用（internal 端點）：下單成功後移除已下單項目。 */
public interface RemoveItemsAfterOrderUseCase {

    void remove(Long userId, List<Long> skuIds);
}

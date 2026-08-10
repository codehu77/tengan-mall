package com.tengan.mall.order.application.port;

import java.util.List;

public interface CartPort {

    /** 呼叫 tengan-cart 的 GET /internal/cart/{userId}/items（只回已勾選項目）。 */
    List<CheckedCartItem> getCheckedItems(Long memberId);

    /** 下單成功後呼叫 DELETE /internal/cart/{userId}/items 移除已下單的項目。 */
    void removeItemsAfterOrder(Long memberId, List<Long> skuIds);
}

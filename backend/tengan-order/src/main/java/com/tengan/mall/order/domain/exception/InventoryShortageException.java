package com.tengan.mall.order.domain.exception;

import java.util.List;

/** 鎖庫存失敗——此時什麼都還沒發生（inventory.lock 內部已自行補償已鎖成功的其他 sku），不需要額外補償。 */
public class InventoryShortageException extends RuntimeException {

    private final List<Long> shortageSkuIds;

    public InventoryShortageException(List<Long> shortageSkuIds) {
        super("庫存不足: skuIds=" + shortageSkuIds);
        this.shortageSkuIds = shortageSkuIds;
    }

    public List<Long> getShortageSkuIds() {
        return shortageSkuIds;
    }
}

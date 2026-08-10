package com.tengan.mall.order.application.port;

import java.util.List;

public interface InventoryPort {

    /** 呼叫 tengan-inventory 的 POST /internal/inventory/lock，以 orderSn 冪等。 */
    LockResult lock(String orderSn, List<LockItem> items);

    /** 呼叫 POST /internal/inventory/release，補償剛才鎖定的庫存（條件式 UPDATE，冪等 no-op 安全）。 */
    void release(String orderSn);
}

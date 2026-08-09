package com.tengan.mall.inventory.domain.repository;

import com.tengan.mall.inventory.domain.model.WareOrderTask;
import java.util.Optional;

public interface WareOrderTaskRepository {

    Optional<WareOrderTask> findByOrderSn(String orderSn);

    /** lock 成功時整張連明細一起寫入，只有新增語意（task 一旦建立，狀態轉換一律走 markReleased/markDeducted）。 */
    WareOrderTask save(WareOrderTask task);

    /** 條件式 UPDATE status=RELEASED WHERE order_sn=? AND status=LOCKED，回傳是否真的轉換成功（搶到這次操作權）。 */
    boolean markReleased(String orderSn);

    /** 條件式 UPDATE status=DEDUCTED WHERE order_sn=? AND status=LOCKED。 */
    boolean markDeducted(String orderSn);
}

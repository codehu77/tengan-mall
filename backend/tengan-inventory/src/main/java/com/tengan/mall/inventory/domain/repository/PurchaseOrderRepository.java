package com.tengan.mall.inventory.domain.repository;

import com.tengan.mall.inventory.domain.model.PurchaseOrder;
import java.util.Map;
import java.util.Optional;

public interface PurchaseOrderRepository {

    Optional<PurchaseOrder> findById(Long id);

    /** 只用於新增（建單），整張連 items 一起寫入。 */
    PurchaseOrder save(PurchaseOrder po);

    /**
     * 條件式 UPDATE status=RECEIVED WHERE id=? AND status=PENDING，同時寫入每個 item 的
     * receivedQty，回傳是否真的搶到收貨權（防重複收貨，見 WareOrderTaskRepository#markReleased 同樣的模式）。
     */
    boolean markReceived(Long poId, Map<Long, Integer> itemIdToReceivedQty);
}

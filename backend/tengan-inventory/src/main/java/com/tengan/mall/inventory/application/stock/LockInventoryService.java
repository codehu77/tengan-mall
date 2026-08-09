package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.model.WareOrderTask;
import com.tengan.mall.inventory.domain.model.WareOrderTaskDetail;
import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * lock 用 orderSn 是否已有對應 ware_order_task 判斷冪等；缺貨時已鎖成功的其他 sku 會被補償釋放，
 * 且不寫入 ware_order_task——下次同 orderSn 重試會用當下最新庫存重新嘗試，語意正確
 * （見 .docs/微服務前台API待開發清單.md「開發細節：庫存鎖定/釋放/扣減」）。
 */
@Service
public class LockInventoryService implements LockInventoryUseCase {

    private final WareOrderTaskRepository wareOrderTaskRepository;
    private final WareSkuRepository wareSkuRepository;

    public LockInventoryService(WareOrderTaskRepository wareOrderTaskRepository, WareSkuRepository wareSkuRepository) {
        this.wareOrderTaskRepository = wareOrderTaskRepository;
        this.wareSkuRepository = wareSkuRepository;
    }

    @Override
    @Transactional
    public LockInventoryResult lock(LockInventoryCommand command) {
        if (wareOrderTaskRepository.findByOrderSn(command.orderSn()).isPresent()) {
            return new LockInventoryResult(true, List.of());
        }

        List<WareOrderTaskDetail> lockedDetails = new ArrayList<>();
        List<Long> shortageSkuIds = new ArrayList<>();
        for (LockInventoryItem item : command.items()) {
            Long wareId = tryLockAnyWarehouse(item.skuId(), item.count());
            if (wareId == null) {
                shortageSkuIds.add(item.skuId());
            } else {
                lockedDetails.add(new WareOrderTaskDetail(null, wareId, item.skuId(), item.count()));
            }
        }

        if (!shortageSkuIds.isEmpty()) {
            for (WareOrderTaskDetail detail : lockedDetails) {
                wareSkuRepository.release(detail.wareId(), detail.skuId(), detail.skuCount());
            }
            return new LockInventoryResult(false, shortageSkuIds);
        }

        wareOrderTaskRepository.save(WareOrderTask.lock(command.orderSn(), lockedDetails));
        return new LockInventoryResult(true, List.of());
    }

    /** first-fit：依 ware_id 排序嘗試候選倉庫，第一個庫存足夠的就用該倉，不做跨倉拆單。 */
    private Long tryLockAnyWarehouse(Long skuId, int count) {
        for (Long wareId : wareSkuRepository.findCandidateWareIds(skuId)) {
            if (wareSkuRepository.tryLock(wareId, skuId, count)) {
                return wareId;
            }
        }
        return null;
    }
}

package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.model.WareOrderTask;
import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 冪等：先用條件式 UPDATE（WHERE status=LOCKED）搶「這次操作權」，只有真的搶到（affected>0）才動
 * ware_sku——這樣即使 MQ 消費者重試、或使用者手動取消跟逾時排程同時觸發，並發呼叫也只有一個會真的
 * 釋放庫存，另一個因為狀態轉換沒搶到而直接 no-op，不會重複釋放。
 *
 * <p>Phase B：明細 {@code wareId==null} 代表這筆是 Redis 閘門保留（見 LockInventoryService），
 * 改呼叫 {@link GateQuotaAdapter#release} 把 semaphore/限購計數還回去。</p>
 */
@Service
public class ReleaseInventoryService implements ReleaseInventoryUseCase {

    private final WareOrderTaskRepository wareOrderTaskRepository;
    private final WareSkuRepository wareSkuRepository;
    private final GateQuotaAdapter gateQuotaAdapter;

    public ReleaseInventoryService(WareOrderTaskRepository wareOrderTaskRepository,
            WareSkuRepository wareSkuRepository, GateQuotaAdapter gateQuotaAdapter) {
        this.wareOrderTaskRepository = wareOrderTaskRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
    }

    @Override
    @Transactional
    public void release(String orderSn) {
        var taskOpt = wareOrderTaskRepository.findByOrderSn(orderSn);
        if (taskOpt.isEmpty()) {
            return;
        }
        if (!wareOrderTaskRepository.markReleased(orderSn)) {
            return;
        }
        WareOrderTask task = taskOpt.get();
        task.getDetails().forEach(d -> {
            if (d.wareId() == null) {
                gateQuotaAdapter.release(d.skuId(), task.getMemberId(), d.skuCount());
            } else {
                wareSkuRepository.release(d.wareId(), d.skuId(), d.skuCount());
            }
        });
    }
}

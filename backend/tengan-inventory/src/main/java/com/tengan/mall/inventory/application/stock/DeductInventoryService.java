package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 冪等機制同 ReleaseInventoryService——條件式狀態轉換先搶操作權，才動 ware_sku。
 *
 * <p>Phase B：明細 {@code wareId==null} 代表 Redis 閘門保留，從來沒動過 locked_stock，
 * 沒有東西可以「轉正」，直接跳過——真正的庫存扣減是閘門結算排程一次性做的，不是每筆訂單付款時做
 * （完全比照秒殺：秒殺訂單付款時也不碰 ware_sku）。</p>
 */
@Service
public class DeductInventoryService implements DeductInventoryUseCase {

    private final WareOrderTaskRepository wareOrderTaskRepository;
    private final WareSkuRepository wareSkuRepository;

    public DeductInventoryService(WareOrderTaskRepository wareOrderTaskRepository,
            WareSkuRepository wareSkuRepository) {
        this.wareOrderTaskRepository = wareOrderTaskRepository;
        this.wareSkuRepository = wareSkuRepository;
    }

    @Override
    @Transactional
    public void deduct(String orderSn) {
        var taskOpt = wareOrderTaskRepository.findByOrderSn(orderSn);
        if (taskOpt.isEmpty()) {
            return;
        }
        if (!wareOrderTaskRepository.markDeducted(orderSn)) {
            return;
        }
        taskOpt.get().getDetails().stream()
                .filter(d -> d.wareId() != null)
                .forEach(d -> wareSkuRepository.deduct(d.wareId(), d.skuId(), d.skuCount()));
    }
}

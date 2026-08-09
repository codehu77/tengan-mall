package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 冪等機制同 ReleaseInventoryService——條件式狀態轉換先搶操作權，才動 ware_sku。 */
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
        taskOpt.get().getDetails()
                .forEach(d -> wareSkuRepository.deduct(d.wareId(), d.skuId(), d.skuCount()));
    }
}

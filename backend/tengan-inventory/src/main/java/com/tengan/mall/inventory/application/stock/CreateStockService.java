package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.exception.WareSkuAlreadyExistsException;
import com.tengan.mall.inventory.domain.model.InventoryOperLog;
import com.tengan.mall.inventory.domain.repository.InventoryOperLogRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 全新 SKU 第一次在某個倉建立庫存列——沒有舊值可覆蓋，用絕對值合理，跟 adjust（delta）是不同語意。 */
@Service
public class CreateStockService implements CreateStockUseCase {

    private final WareSkuRepository wareSkuRepository;
    private final InventoryOperLogRepository inventoryOperLogRepository;

    public CreateStockService(WareSkuRepository wareSkuRepository,
            InventoryOperLogRepository inventoryOperLogRepository) {
        this.wareSkuRepository = wareSkuRepository;
        this.inventoryOperLogRepository = inventoryOperLogRepository;
    }

    @Override
    @Transactional
    public void create(CreateStockCommand command) {
        boolean created = wareSkuRepository.tryCreateInitialStock(command.wareId(), command.skuId(),
                command.initialStock());
        if (!created) {
            throw new WareSkuAlreadyExistsException(command.wareId(), command.skuId());
        }

        inventoryOperLogRepository.save(InventoryOperLog.create(command.operator(), "stock", "create",
                "新增庫存 wareId=" + command.wareId() + " skuId=" + command.skuId() + " initialStock="
                        + command.initialStock()));
    }
}

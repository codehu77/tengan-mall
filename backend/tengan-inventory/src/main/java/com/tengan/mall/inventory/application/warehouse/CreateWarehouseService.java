package com.tengan.mall.inventory.application.warehouse;

import com.tengan.mall.inventory.domain.model.InventoryOperLog;
import com.tengan.mall.inventory.domain.model.WareInfo;
import com.tengan.mall.inventory.domain.repository.InventoryOperLogRepository;
import com.tengan.mall.inventory.domain.repository.WareInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWarehouseService implements CreateWarehouseUseCase {

    private final WareInfoRepository wareInfoRepository;
    private final InventoryOperLogRepository inventoryOperLogRepository;

    public CreateWarehouseService(WareInfoRepository wareInfoRepository,
            InventoryOperLogRepository inventoryOperLogRepository) {
        this.wareInfoRepository = wareInfoRepository;
        this.inventoryOperLogRepository = inventoryOperLogRepository;
    }

    @Override
    @Transactional
    public CreateWarehouseResult create(CreateWarehouseCommand command) {
        WareInfo saved = wareInfoRepository.save(WareInfo.create(command.name(), command.address()));

        inventoryOperLogRepository.save(InventoryOperLog.create(command.operator(), "warehouse", "create",
                "新增倉庫 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateWarehouseResult(saved.getId());
    }
}

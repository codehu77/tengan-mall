package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.model.InventoryOperLog;
import com.tengan.mall.inventory.domain.repository.InventoryOperLogRepository;
import java.time.ZoneId;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryOperLogRepositoryImpl implements InventoryOperLogRepository {

    private final InventoryOperLogMapper inventoryOperLogMapper;

    public InventoryOperLogRepositoryImpl(InventoryOperLogMapper inventoryOperLogMapper) {
        this.inventoryOperLogMapper = inventoryOperLogMapper;
    }

    @Override
    public InventoryOperLog save(InventoryOperLog operLog) {
        InventoryOperLogPO po = new InventoryOperLogPO();
        po.setOperator(operLog.getOperator());
        po.setModule(operLog.getModule());
        po.setAction(operLog.getAction());
        po.setTargetDesc(operLog.getTargetDesc());
        po.setCreatedAt(operLog.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        inventoryOperLogMapper.insert(po);
        operLog.assignId(po.getId());
        return operLog;
    }
}

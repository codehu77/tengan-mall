package com.tengan.mall.inventory.domain.repository;

import com.tengan.mall.inventory.domain.model.InventoryOperLog;

public interface InventoryOperLogRepository {

    InventoryOperLog save(InventoryOperLog operLog);
}

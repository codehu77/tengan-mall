package com.tengan.mall.inventory.application.stock;

public interface LockInventoryUseCase {

    LockInventoryResult lock(LockInventoryCommand command);
}

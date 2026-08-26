package com.tengan.mall.inventory.application.gate;

public interface SettleGatesUseCase {

    /** @return 這次結算成功的 sku 數 */
    int settle();
}

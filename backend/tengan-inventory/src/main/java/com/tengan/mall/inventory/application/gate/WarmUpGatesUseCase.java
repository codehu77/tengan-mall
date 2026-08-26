package com.tengan.mall.inventory.application.gate;

public interface WarmUpGatesUseCase {

    /** @return 這次預熱處理的 sku 數 */
    int warmUp();
}

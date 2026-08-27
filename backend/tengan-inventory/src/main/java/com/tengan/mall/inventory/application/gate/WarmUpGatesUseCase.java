package com.tengan.mall.inventory.application.gate;

public interface WarmUpGatesUseCase {

    /** @return 這次預熱處理的 sku 數 */
    int warmUp();

    /** 「啟用/重設防超賣保護」用：立即同步預熱單一 sku，不等排程、不套用候選篩選條件。 */
    void warmUpOne(Long skuId);
}

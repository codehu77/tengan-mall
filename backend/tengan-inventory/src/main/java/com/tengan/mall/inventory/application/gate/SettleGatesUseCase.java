package com.tengan.mall.inventory.application.gate;

public interface SettleGatesUseCase {

    /** @return 這次結算成功的 sku 數 */
    int settle();

    /**
     * 「重設防超賣保護」用：不管 gate_close_time 是否已經到，強制把指定 sku 目前這一輪（已預熱、尚未
     * 結算）立刻結算收尾。這顆 sku 沒有進行中的一輪（沒預熱過，或已經結算過了）就是 no-op，回傳 false。
     */
    boolean settleOne(Long skuId);
}

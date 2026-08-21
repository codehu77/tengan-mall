package com.tengan.mall.seckill.application.port;

/** 呼叫 tengan-inventory 的 /internal/inventory/seckill/deduct（見規劃第 6 節結算）。 */
public interface InventoryPort {

    void seckillDeduct(Long skuId, int count);
}

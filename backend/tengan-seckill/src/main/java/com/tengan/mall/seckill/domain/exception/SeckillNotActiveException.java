package com.tengan.mall.seckill.domain.exception;

/**
 * 這顆 SKU 現在沒有對應的活躍秒殺 Redis key（{@code seckill:sku:{skuId}} 不存在或已過 TTL）——
 * 呼叫端（tengan-order 的訂單建立 Saga）收到這個例外，應該落回一般商品的 {@code inventoryPort.lock()}
 * 路徑，不是把它當成錯誤處理（見 Phase 9 規劃第 4.2 節「不需要活動已結束這種錯誤」）。
 */
public class SeckillNotActiveException extends RuntimeException {

    public SeckillNotActiveException(Long skuId) {
        super("skuId=" + skuId + " 目前沒有活躍的秒殺活動");
    }
}

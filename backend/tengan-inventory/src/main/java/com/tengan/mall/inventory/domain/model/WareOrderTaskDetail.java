package com.tengan.mall.inventory.domain.model;

/**
 * WareOrderTask 底下的明細列，沒有自己的狀態欄位——release/deduct 的 request body 只帶
 * {orderSn}（沒有逐項 id），代表整張訂單的鎖定狀態是一起轉換的（比照 Spu 底下 SkuImage 那種
 * 沒有獨立生命週期意義的淺子集合，用 record 就好，不需要獨立 Repository）。
 */
public record WareOrderTaskDetail(Long id, Long wareId, Long skuId, int skuCount) {
}

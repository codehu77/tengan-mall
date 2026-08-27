package com.tengan.mall.inventory.domain.model;

import java.time.LocalDateTime;

/**
 * 從 tengan-product 單向同步過來的本地副本，只保留 lock() 校驗時要用的欄位。
 * gateProtectedStock/gateWarmedAt/gateSettledAt 是 tengan-inventory 自己的閘門生命週期狀態，
 * 不是從 product 同步過來的，product 那邊的 upsert 也絕對不會覆寫這三欄。
 */
public record SkuLaunchConfig(Long skuId, Long spuId, LocalDateTime saleStartTime, boolean trafficGateEnabled,
        LocalDateTime gateCloseTime, Integer purchaseLimitPerUser, Integer gateProtectedStock,
        LocalDateTime gateWarmedAt, LocalDateTime gateSettledAt) {

    /** 閘門是否正在保護中：開啟 + 已預熱 + 尚未關閉。 */
    public boolean isGateActive(LocalDateTime now) {
        return trafficGateEnabled && gateWarmedAt != null && gateCloseTime != null && now.isBefore(gateCloseTime);
    }
}

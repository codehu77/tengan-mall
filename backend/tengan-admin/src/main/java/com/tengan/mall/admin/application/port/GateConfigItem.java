package com.tengan.mall.admin.application.port;

import java.time.LocalDateTime;

/** synced=false 代表這顆 skuId 還沒同步過商品的開賣時間設定，不是後端出錯。 */
public record GateConfigItem(Long skuId, boolean synced, boolean trafficGateEnabled, LocalDateTime saleStartTime,
        LocalDateTime gateCloseTime, LocalDateTime gateWarmedAt, LocalDateTime gateSettledAt) {
}

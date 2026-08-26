package com.tengan.mall.inventory.application.gate;

import java.time.LocalDateTime;

/**
 * 供 tengan-admin 庫存頁面「設定庫存流量閘門」對話框開啟時回填用。skuId 還沒同步過商品設定時
 * synced=false，其餘欄位都是 null/false，前端顯示「尚未設定開賣時間」而不是報錯。
 */
public record GateConfigView(Long skuId, boolean synced, boolean trafficGateEnabled, LocalDateTime saleStartTime,
        LocalDateTime gateCloseTime, LocalDateTime gateWarmedAt, LocalDateTime gateSettledAt) {
}

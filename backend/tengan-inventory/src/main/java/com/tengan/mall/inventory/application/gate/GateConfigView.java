package com.tengan.mall.inventory.application.gate;

import java.time.LocalDateTime;

/**
 * 供 tengan-admin SPU 列表頁「防超賣保護」欄位回填用，一個 spuId 一筆代表——同一 SPU 底下的 SKU
 * 都是同一顆按鈕 fan-out 設定的，理論上永遠同步，gateWarmedAt 為 null 代表從未啟用過。
 */
public record GateConfigView(Long spuId, LocalDateTime gateWarmedAt, LocalDateTime gateCloseTime) {
}

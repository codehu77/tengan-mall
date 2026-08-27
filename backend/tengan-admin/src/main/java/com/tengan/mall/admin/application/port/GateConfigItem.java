package com.tengan.mall.admin.application.port;

import java.time.LocalDateTime;

/** SPU 列表頁「防超賣保護」欄位用，gateWarmedAt 為 null 代表這顆 SPU 從未啟用過。 */
public record GateConfigItem(Long spuId, LocalDateTime gateWarmedAt, LocalDateTime gateCloseTime) {
}

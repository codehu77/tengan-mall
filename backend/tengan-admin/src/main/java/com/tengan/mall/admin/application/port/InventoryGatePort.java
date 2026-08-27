package com.tengan.mall.admin.application.port;

import java.time.LocalDateTime;
import java.util.List;

/** 呼叫 tengan-inventory 的「即將開賣」庫存流量閘門(Phase B)監控/手動操作 internal 端點。 */
public interface InventoryGatePort {

    List<GateStatusItem> listGates();

    /** @return 這次觸發實際預熱的 sku 數 */
    int triggerWarmUpNow();

    /** SPU 列表頁「防超賣保護」欄位批次回填用。 */
    List<GateConfigItem> getGatesBySpuIds(List<Long> spuIds);

    /** 管理員在 SPU 列表頁「啟用/重設防超賣保護」，一顆 SPU 底下每顆 SKU 各呼叫一次。 */
    void configureGate(Long skuId, LocalDateTime gateCloseTime, String operatorToken);
}

package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-inventory 的「即將開賣」庫存流量閘門(Phase B)監控/手動操作 internal 端點。 */
public interface InventoryGatePort {

    List<GateStatusItem> listGates();

    /** @return 這次觸發實際預熱的 sku 數 */
    int triggerWarmUpNow();

    /** 庫存頁面「設定庫存流量閘門」對話框開啟時回填目前設定用。 */
    GateConfigItem getGate(Long skuId);

    /** 管理員在庫存頁面直接設定庫存流量閘門。 */
    void configureGate(Long skuId, ConfigureGatePayload payload, String operatorToken);
}

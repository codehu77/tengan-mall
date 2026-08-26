package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-inventory 的「即將開賣」流量閘門(Phase B)監控/手動操作 internal 端點。 */
public interface InventoryGatePort {

    List<GateStatusItem> listGates();

    /** @return 這次觸發實際預熱的 sku 數 */
    int triggerWarmUpNow();
}

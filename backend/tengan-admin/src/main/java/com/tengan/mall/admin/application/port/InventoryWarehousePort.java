package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-inventory 的倉庫 internal 端點，跟 {@link ProductBrandPort} 同樣的純代理原則。 */
public interface InventoryWarehousePort {

    List<WarehouseItem> listWarehouses();

    Long createWarehouse(CreateWarehousePayload payload, String operatorToken);
}

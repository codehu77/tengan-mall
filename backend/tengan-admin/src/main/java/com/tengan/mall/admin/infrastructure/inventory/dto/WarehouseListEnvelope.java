package com.tengan.mall.admin.infrastructure.inventory.dto;

import com.tengan.mall.admin.application.port.WarehouseItem;
import java.util.List;

public record WarehouseListEnvelope(List<WarehouseItem> items) {
}

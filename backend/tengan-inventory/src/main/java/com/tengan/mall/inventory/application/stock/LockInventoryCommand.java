package com.tengan.mall.inventory.application.stock;

import java.util.List;

public record LockInventoryCommand(String orderSn, List<LockInventoryItem> items) {
}

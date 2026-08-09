package com.tengan.mall.inventory.application.stock;

import java.util.List;

public record LockInventoryResult(boolean success, List<Long> shortageSkuIds) {
}

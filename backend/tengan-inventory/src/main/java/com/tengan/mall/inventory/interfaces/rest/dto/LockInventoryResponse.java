package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record LockInventoryResponse(boolean success, List<Long> shortageSkuIds) {
}

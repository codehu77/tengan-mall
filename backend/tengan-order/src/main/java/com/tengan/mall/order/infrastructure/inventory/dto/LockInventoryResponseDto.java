package com.tengan.mall.order.infrastructure.inventory.dto;

import java.util.List;

public record LockInventoryResponseDto(boolean success, List<Long> shortageSkuIds) {
}

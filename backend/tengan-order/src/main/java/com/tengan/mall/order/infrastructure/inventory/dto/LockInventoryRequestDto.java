package com.tengan.mall.order.infrastructure.inventory.dto;

import java.util.List;

public record LockInventoryRequestDto(String orderSn, List<LockInventoryItemDto> items) {
}

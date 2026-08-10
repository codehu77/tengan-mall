package com.tengan.mall.order.interfaces.rest.dto;

import java.util.List;

public record InventoryShortageResponse(String message, List<Long> shortageSkuIds) {
}

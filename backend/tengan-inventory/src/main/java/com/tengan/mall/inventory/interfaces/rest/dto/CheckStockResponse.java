package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record CheckStockResponse(boolean allSufficient, List<CheckStockItemResponse> items) {
}

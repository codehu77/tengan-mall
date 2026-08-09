package com.tengan.mall.inventory.application.stock;

import java.util.List;

public record CheckStockCommand(List<CheckStockItem> items) {
}

package com.tengan.mall.inventory.application.stock;

import java.util.List;

public record CheckStockResult(boolean allSufficient, List<CheckStockLineResult> items) {
}

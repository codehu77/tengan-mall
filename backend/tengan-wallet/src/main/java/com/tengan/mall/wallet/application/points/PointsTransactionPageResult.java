package com.tengan.mall.wallet.application.points;

import java.util.List;

public record PointsTransactionPageResult(List<PointsTransactionView> items, long total) {
}

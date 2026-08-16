package com.tengan.mall.wallet.interfaces.rest.dto;

import java.util.List;

public record PointsTransactionListResponse(List<PointsTransactionResponse> items, long total) {
}

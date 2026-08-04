package com.tengan.mall.admin.application.port;

import java.util.List;

public record SpuSearchResult(List<SpuSummaryItem> items, long total) {
}

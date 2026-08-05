package com.tengan.mall.search.application;

import java.util.List;

public record SearchSkusResult(List<SkuSearchItem> items, long total, int page, int pageSize,
        SearchAggregations aggregations) {
}

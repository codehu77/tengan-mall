package com.tengan.mall.search.interfaces.rest.dto;

import java.util.List;

public record SearchResponse(List<SearchItemResponse> items, long total, int page, int pageSize,
        SearchAggregationsResponse aggregations) {
}

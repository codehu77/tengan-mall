package com.tengan.mall.search.interfaces.rest.dto;

import java.util.List;

public record SearchAggregationsResponse(List<BrandAggResponse> brands, List<AttrAggResponse> attrs) {
}

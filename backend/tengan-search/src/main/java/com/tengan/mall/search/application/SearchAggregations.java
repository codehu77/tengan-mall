package com.tengan.mall.search.application;

import java.util.List;

public record SearchAggregations(List<BrandAggItem> brands, List<AttrAggItem> attrs) {
}

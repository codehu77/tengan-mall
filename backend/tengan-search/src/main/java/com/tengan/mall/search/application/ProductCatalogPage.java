package com.tengan.mall.search.application;

import java.util.List;

public record ProductCatalogPage(List<ProductCatalogSkuItem> skus, boolean hasNext) {
}

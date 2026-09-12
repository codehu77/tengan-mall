package com.tengan.mall.search.application;

import java.util.List;

public record ProductCatalogPage(List<ProductCatalogSpuItem> spus, boolean hasNext) {
}

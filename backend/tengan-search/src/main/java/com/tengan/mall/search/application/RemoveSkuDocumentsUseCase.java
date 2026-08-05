package com.tengan.mall.search.application;

import java.util.List;

public interface RemoveSkuDocumentsUseCase {

    void remove(List<Long> skuIds);
}

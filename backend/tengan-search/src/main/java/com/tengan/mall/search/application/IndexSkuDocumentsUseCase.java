package com.tengan.mall.search.application;

import java.util.List;

public interface IndexSkuDocumentsUseCase {

    void index(List<SkuSearchDocument> documents);
}

package com.tengan.mall.product.application.spu;

import org.springframework.stereotype.Service;

@Service
public class ListSpusService implements ListSpusUseCase {

    private final SpuSearchPort spuSearchPort;

    public ListSpusService(SpuSearchPort spuSearchPort) {
        this.spuSearchPort = spuSearchPort;
    }

    @Override
    public ListSpusResult search(SearchSpusQuery query) {
        SpuSearchCriteria criteria = new SpuSearchCriteria(query.categoryId(), query.brandId(), query.name(),
                query.status());
        var items = spuSearchPort.search(criteria, query.pageNum(), query.pageSize());
        return new ListSpusResult(items, spuSearchPort.countSearch(criteria));
    }
}

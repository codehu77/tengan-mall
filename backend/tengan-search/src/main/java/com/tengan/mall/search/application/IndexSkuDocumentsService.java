package com.tengan.mall.search.application;

import com.tengan.mall.search.infrastructure.elasticsearch.SkuSearchRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IndexSkuDocumentsService implements IndexSkuDocumentsUseCase {

    private final SkuSearchRepository skuSearchRepository;

    public IndexSkuDocumentsService(SkuSearchRepository skuSearchRepository) {
        this.skuSearchRepository = skuSearchRepository;
    }

    @Override
    public void index(List<SkuSearchDocument> documents) {
        skuSearchRepository.saveAll(documents);
    }
}

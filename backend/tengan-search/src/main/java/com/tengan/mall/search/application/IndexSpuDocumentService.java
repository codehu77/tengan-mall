package com.tengan.mall.search.application;

import com.tengan.mall.search.infrastructure.elasticsearch.SpuSearchRepository;
import org.springframework.stereotype.Service;

@Service
public class IndexSpuDocumentService implements IndexSpuDocumentUseCase {

    private final SpuSearchRepository spuSearchRepository;

    public IndexSpuDocumentService(SpuSearchRepository spuSearchRepository) {
        this.spuSearchRepository = spuSearchRepository;
    }

    @Override
    public void index(SpuSearchDocument document) {
        spuSearchRepository.save(document);
    }
}

package com.tengan.mall.search.application;

import com.tengan.mall.search.infrastructure.elasticsearch.SpuSearchRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveSpuDocumentService implements RemoveSpuDocumentUseCase {

    private final SpuSearchRepository spuSearchRepository;

    public RemoveSpuDocumentService(SpuSearchRepository spuSearchRepository) {
        this.spuSearchRepository = spuSearchRepository;
    }

    @Override
    public void remove(Long spuId) {
        spuSearchRepository.deleteById(spuId);
    }
}

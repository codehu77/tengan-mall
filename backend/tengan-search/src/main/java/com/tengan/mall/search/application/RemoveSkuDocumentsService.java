package com.tengan.mall.search.application;

import com.tengan.mall.search.infrastructure.elasticsearch.SkuSearchRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RemoveSkuDocumentsService implements RemoveSkuDocumentsUseCase {

    private final SkuSearchRepository skuSearchRepository;

    public RemoveSkuDocumentsService(SkuSearchRepository skuSearchRepository) {
        this.skuSearchRepository = skuSearchRepository;
    }

    @Override
    public void remove(List<Long> skuIds) {
        skuSearchRepository.deleteAllById(skuIds);
    }
}

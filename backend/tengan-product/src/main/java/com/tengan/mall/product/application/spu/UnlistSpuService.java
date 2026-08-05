package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnlistSpuService implements UnlistSpuUseCase {

    private final SpuRepository spuRepository;
    private final ProductSearchEventPublisherPort searchEventPublisher;

    public UnlistSpuService(SpuRepository spuRepository, ProductSearchEventPublisherPort searchEventPublisher) {
        this.spuRepository = spuRepository;
        this.searchEventPublisher = searchEventPublisher;
    }

    @Override
    @Transactional
    public void unlist(UnlistSpuCommand command) {
        Spu spu = spuRepository.findById(command.spuId())
                .orElseThrow(() -> new SpuNotFoundException(command.spuId()));
        spu.unlist();
        spuRepository.save(spu);
        searchEventPublisher.publishRemoved(spu.getId(), spu.getSkus().stream().map(sku -> sku.getId()).toList());
    }
}

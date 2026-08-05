package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishSpuService implements PublishSpuUseCase {

    private final SpuRepository spuRepository;
    private final SpuSearchDocumentAssembler searchDocumentAssembler;
    private final ProductSearchEventPublisherPort searchEventPublisher;

    public PublishSpuService(SpuRepository spuRepository, SpuSearchDocumentAssembler searchDocumentAssembler,
            ProductSearchEventPublisherPort searchEventPublisher) {
        this.spuRepository = spuRepository;
        this.searchDocumentAssembler = searchDocumentAssembler;
        this.searchEventPublisher = searchEventPublisher;
    }

    @Override
    @Transactional
    public void publish(PublishSpuCommand command) {
        Spu spu = spuRepository.findById(command.spuId())
                .orElseThrow(() -> new SpuNotFoundException(command.spuId()));
        spu.publish();
        spuRepository.save(spu);
        searchEventPublisher.publishUpserted(searchDocumentAssembler.assemble(spu));
    }
}

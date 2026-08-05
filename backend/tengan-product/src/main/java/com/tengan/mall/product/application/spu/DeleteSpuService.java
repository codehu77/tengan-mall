package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.exception.SpuOnShelfException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSpuService implements DeleteSpuUseCase {

    private final SpuRepository spuRepository;
    private final ProductSearchEventPublisherPort searchEventPublisher;

    public DeleteSpuService(SpuRepository spuRepository, ProductSearchEventPublisherPort searchEventPublisher) {
        this.spuRepository = spuRepository;
        this.searchEventPublisher = searchEventPublisher;
    }

    @Override
    @Transactional
    public void delete(DeleteSpuCommand command) {
        Spu spu = spuRepository.findById(command.spuId())
                .orElseThrow(() -> new SpuNotFoundException(command.spuId()));
        if (spu.getStatus() == SpuStatus.ON_SHELF) {
            throw new SpuOnShelfException(command.spuId());
        }
        spuRepository.deleteById(command.spuId());
        // 防禦性發送：上架中不可刪的守衛已經擋在前面，這裡多半是刪除本來就不在索引裡的 spu，
        // tengan-search 收到刪除一個不存在的文件是冪等操作，不會出錯。
        searchEventPublisher.publishRemoved(spu.getId(), spu.getSkus().stream().map(sku -> sku.getId()).toList());
    }
}

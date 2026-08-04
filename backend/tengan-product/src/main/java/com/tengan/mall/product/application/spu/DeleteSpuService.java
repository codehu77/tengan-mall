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

    public DeleteSpuService(SpuRepository spuRepository) {
        this.spuRepository = spuRepository;
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
    }
}

package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DuplicateSpuService implements DuplicateSpuUseCase {

    private final SpuRepository spuRepository;

    public DuplicateSpuService(SpuRepository spuRepository) {
        this.spuRepository = spuRepository;
    }

    @Override
    @Transactional
    public DuplicateSpuResult duplicate(Long spuId) {
        Spu source = spuRepository.findById(spuId).orElseThrow(() -> new SpuNotFoundException(spuId));
        Spu duplicate = Spu.duplicateOf(source, source.getName() + " 的副本");
        Spu saved = spuRepository.save(duplicate);
        return new DuplicateSpuResult(saved.getId());
    }
}

package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;

/** 內部用：不論 Spu 目前是什麼狀態（NEW/ON_SHELF/OFF_SHELF）都回傳，給後台編輯表單使用。 */
@Service
public class GetSpuDetailService implements GetSpuDetailUseCase {

    private final SpuRepository spuRepository;
    private final SpuDetailAssembler assembler;

    public GetSpuDetailService(SpuRepository spuRepository, SpuDetailAssembler assembler) {
        this.spuRepository = spuRepository;
        this.assembler = assembler;
    }

    @Override
    public GetSpuDetailResult get(Long spuId) {
        var spu = spuRepository.findById(spuId).orElseThrow(() -> new SpuNotFoundException(spuId));
        return assembler.toResult(spu);
    }
}

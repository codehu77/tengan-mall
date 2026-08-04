package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.SpuRepository;
import org.springframework.stereotype.Service;

/**
 * 前台用：跟內部版查詢範圍不同——只回傳 ON_SHELF 的 Spu，NEW/OFF_SHELF 一律當作「找不到」
 * 隱藏其存在，不是回傳但少幾個欄位，所以獨立成一個 use case（見 ddd-standards.md 第四節判準）。
 */
@Service
public class GetPublicSpuDetailService implements GetPublicSpuDetailUseCase {

    private final SpuRepository spuRepository;
    private final SpuDetailAssembler assembler;

    public GetPublicSpuDetailService(SpuRepository spuRepository, SpuDetailAssembler assembler) {
        this.spuRepository = spuRepository;
        this.assembler = assembler;
    }

    @Override
    public GetSpuDetailResult get(Long spuId) {
        var spu = spuRepository.findById(spuId).orElseThrow(() -> new SpuNotFoundException(spuId));
        if (spu.getStatus() != SpuStatus.ON_SHELF) {
            throw new SpuNotFoundException(spuId);
        }
        return assembler.toResult(spu);
    }
}

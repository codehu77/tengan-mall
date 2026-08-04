package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SkuNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetPublicSkuDetailService implements GetPublicSkuDetailUseCase {

    private final SkuDetailPort skuDetailPort;

    public GetPublicSkuDetailService(SkuDetailPort skuDetailPort) {
        this.skuDetailPort = skuDetailPort;
    }

    @Override
    public SkuDetailView get(Long skuId) {
        return skuDetailPort.findById(skuId).orElseThrow(() -> new SkuNotFoundException(skuId));
    }
}

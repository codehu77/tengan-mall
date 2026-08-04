package com.tengan.mall.product.domain.exception;

public class SpuHasNoSkuException extends RuntimeException {

    public SpuHasNoSkuException(Long spuId) {
        super("Spu 底下沒有任何 Sku，無法上架: " + spuId);
    }
}

package com.tengan.mall.product.domain.exception;

public class SpuNotOnShelfException extends RuntimeException {

    public SpuNotOnShelfException(Long spuId) {
        super("Spu 目前不是上架狀態，無法下架: " + spuId);
    }
}

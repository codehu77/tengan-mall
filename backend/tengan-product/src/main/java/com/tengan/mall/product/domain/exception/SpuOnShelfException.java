package com.tengan.mall.product.domain.exception;

public class SpuOnShelfException extends RuntimeException {

    public SpuOnShelfException(Long spuId) {
        super("Spu 目前上架中，需先下架才能刪除: " + spuId);
    }
}

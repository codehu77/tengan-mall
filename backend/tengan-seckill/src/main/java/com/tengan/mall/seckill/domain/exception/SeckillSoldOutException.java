package com.tengan.mall.seckill.domain.exception;

public class SeckillSoldOutException extends RuntimeException {

    public SeckillSoldOutException(Long skuId) {
        super("skuId=" + skuId + " 秒殺配額已搶完");
    }
}

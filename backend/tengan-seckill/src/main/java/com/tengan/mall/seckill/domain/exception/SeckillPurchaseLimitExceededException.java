package com.tengan.mall.seckill.domain.exception;

public class SeckillPurchaseLimitExceededException extends RuntimeException {

    public SeckillPurchaseLimitExceededException(Long skuId, int limitPerUser) {
        super("skuId=" + skuId + " 已超過每人限購 " + limitPerUser + " 個");
    }
}

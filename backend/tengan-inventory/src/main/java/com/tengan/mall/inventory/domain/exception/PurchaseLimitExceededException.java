package com.tengan.mall.inventory.domain.exception;

/** 本次購買數量會超過該會員對這顆 SKU 的每人限購上限。 */
public class PurchaseLimitExceededException extends RuntimeException {

    public PurchaseLimitExceededException(Long skuId) {
        super("超過每人限購數量: skuId=" + skuId);
    }
}

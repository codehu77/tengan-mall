package com.tengan.mall.order.domain.exception;

import java.util.List;

/** 本次下單數量會超過會員對這些 SKU 的每人限購上限，此時什麼都還沒發生，不需要額外補償。 */
public class PurchaseLimitExceededException extends RuntimeException {

    private final List<Long> skuIds;

    public PurchaseLimitExceededException(List<Long> skuIds) {
        super("超過每人限購數量: skuIds=" + skuIds);
        this.skuIds = skuIds;
    }

    public List<Long> getSkuIds() {
        return skuIds;
    }
}

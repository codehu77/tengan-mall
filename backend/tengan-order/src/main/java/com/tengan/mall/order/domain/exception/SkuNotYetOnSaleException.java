package com.tengan.mall.order.domain.exception;

import java.util.List;

/** 購物車裡有 SKU 的 sale_start_time 還沒到，此時什麼都還沒發生，不需要額外補償。 */
public class SkuNotYetOnSaleException extends RuntimeException {

    public SkuNotYetOnSaleException(List<Long> skuIds) {
        super("商品尚未開賣: skuIds=" + skuIds);
    }
}

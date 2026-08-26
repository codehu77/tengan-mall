package com.tengan.mall.inventory.domain.exception;

/** 商品的 sale_start_time 還沒到，目前這個時間點不可購買。 */
public class SkuNotYetOnSaleException extends RuntimeException {

    public SkuNotYetOnSaleException(Long skuId) {
        super("商品尚未開賣: skuId=" + skuId);
    }
}

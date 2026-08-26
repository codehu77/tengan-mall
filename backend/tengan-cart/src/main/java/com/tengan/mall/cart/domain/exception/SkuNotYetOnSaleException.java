package com.tengan.mall.cart.domain.exception;

/** 商品的 sale_start_time 還沒到，目前不可加入購物車。 */
public class SkuNotYetOnSaleException extends RuntimeException {

    public SkuNotYetOnSaleException(Long skuId) {
        super("商品尚未開賣: skuId=" + skuId);
    }
}

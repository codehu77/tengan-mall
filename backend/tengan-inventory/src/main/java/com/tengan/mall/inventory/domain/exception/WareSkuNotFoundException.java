package com.tengan.mall.inventory.domain.exception;

/** 該 (wareId, skuId) 還沒有任何庫存列——調整前要先新增。 */
public class WareSkuNotFoundException extends RuntimeException {

    public WareSkuNotFoundException(Long wareId, Long skuId) {
        super("尚未建立庫存列，請先新增: wareId=" + wareId + " skuId=" + skuId);
    }
}

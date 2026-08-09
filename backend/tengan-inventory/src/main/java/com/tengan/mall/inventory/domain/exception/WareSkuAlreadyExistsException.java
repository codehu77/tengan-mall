package com.tengan.mall.inventory.domain.exception;

/** 該 (wareId, skuId) 已經有庫存列——新增庫存不能覆蓋既有紀錄，要調整請用 adjust。 */
public class WareSkuAlreadyExistsException extends RuntimeException {

    public WareSkuAlreadyExistsException(Long wareId, Long skuId) {
        super("此倉庫已經有這顆 SKU 的庫存紀錄，請改用調整: wareId=" + wareId + " skuId=" + skuId);
    }
}

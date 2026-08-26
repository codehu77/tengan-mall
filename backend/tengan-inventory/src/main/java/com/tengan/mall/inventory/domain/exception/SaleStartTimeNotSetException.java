package com.tengan.mall.inventory.domain.exception;

/** 啟用庫存流量閘門前必須先有開賣時間，閘門關閉時間才有一個可以比較的基準。 */
public class SaleStartTimeNotSetException extends RuntimeException {

    public SaleStartTimeNotSetException(Long skuId) {
        super("請先在商品設定開賣時間，才能啟用庫存流量閘門: skuId=" + skuId);
    }
}

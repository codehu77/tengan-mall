package com.tengan.mall.inventory.domain.exception;

/** 庫存流量閘門關閉時間必須晚於開賣時間，不然閘門一開就已經關了，毫無保護意義。 */
public class InvalidGateCloseTimeException extends RuntimeException {

    public InvalidGateCloseTimeException(Long skuId) {
        super("閘門關閉時間必須晚於開賣時間: skuId=" + skuId);
    }
}

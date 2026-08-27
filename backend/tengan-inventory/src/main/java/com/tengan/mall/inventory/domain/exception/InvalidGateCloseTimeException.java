package com.tengan.mall.inventory.domain.exception;

/** 保護結束時間必須晚於現在，不然一啟用就已經結束了，毫無保護意義。 */
public class InvalidGateCloseTimeException extends RuntimeException {

    public InvalidGateCloseTimeException(Long skuId) {
        super("保護結束時間必須晚於現在: skuId=" + skuId);
    }
}

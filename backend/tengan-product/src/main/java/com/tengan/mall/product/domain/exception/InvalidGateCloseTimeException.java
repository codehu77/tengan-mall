package com.tengan.mall.product.domain.exception;

public class InvalidGateCloseTimeException extends RuntimeException {

    public InvalidGateCloseTimeException(Long spuId) {
        super("啟用流量閘門時，gateCloseTime 必須晚於 saleStartTime: " + spuId);
    }
}

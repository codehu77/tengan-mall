package com.tengan.mall.product.domain.exception;

public class InvalidTeaserRemoveTimeException extends RuntimeException {

    public InvalidTeaserRemoveTimeException(Long spuId) {
        super("啟用首頁預告時，teaserRemoveAt 必須晚於 saleStartTime: " + spuId);
    }
}

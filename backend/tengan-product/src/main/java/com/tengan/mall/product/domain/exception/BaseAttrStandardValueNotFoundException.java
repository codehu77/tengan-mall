package com.tengan.mall.product.domain.exception;

public class BaseAttrStandardValueNotFoundException extends RuntimeException {

    public BaseAttrStandardValueNotFoundException(Long id) {
        super("找不到 BaseAttrStandardValue: " + id);
    }
}

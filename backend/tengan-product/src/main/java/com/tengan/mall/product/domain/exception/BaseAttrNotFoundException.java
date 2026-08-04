package com.tengan.mall.product.domain.exception;

public class BaseAttrNotFoundException extends RuntimeException {

    public BaseAttrNotFoundException(Long id) {
        super("找不到 BaseAttr: " + id);
    }
}

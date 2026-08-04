package com.tengan.mall.product.domain.exception;

public class BaseAttrGroupNotFoundException extends RuntimeException {

    public BaseAttrGroupNotFoundException(Long id) {
        super("找不到 BaseAttrGroup: " + id);
    }
}

package com.tengan.mall.product.domain.exception;

public class SaleAttrNotFoundException extends RuntimeException {

    public SaleAttrNotFoundException(Long id) {
        super("找不到 SaleAttr: " + id);
    }
}

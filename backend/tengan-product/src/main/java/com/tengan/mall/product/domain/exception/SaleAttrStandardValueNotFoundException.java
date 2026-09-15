package com.tengan.mall.product.domain.exception;

public class SaleAttrStandardValueNotFoundException extends RuntimeException {

    public SaleAttrStandardValueNotFoundException(Long id) {
        super("找不到 SaleAttrStandardValue: " + id);
    }
}

package com.tengan.mall.product.domain.exception;

public class SkuNotFoundException extends RuntimeException {

    public SkuNotFoundException(Long id) {
        super("找不到 Sku: " + id);
    }
}

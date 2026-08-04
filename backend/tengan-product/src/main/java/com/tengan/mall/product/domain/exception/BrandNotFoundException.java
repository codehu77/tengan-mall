package com.tengan.mall.product.domain.exception;

public class BrandNotFoundException extends RuntimeException {

    public BrandNotFoundException(Long id) {
        super("品牌不存在: " + id);
    }
}

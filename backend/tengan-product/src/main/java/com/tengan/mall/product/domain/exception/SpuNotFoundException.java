package com.tengan.mall.product.domain.exception;

public class SpuNotFoundException extends RuntimeException {

    public SpuNotFoundException(Long id) {
        super("找不到 Spu: " + id);
    }
}

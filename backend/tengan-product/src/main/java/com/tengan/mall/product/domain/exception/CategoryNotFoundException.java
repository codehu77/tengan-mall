package com.tengan.mall.product.domain.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("分類不存在: " + id);
    }
}

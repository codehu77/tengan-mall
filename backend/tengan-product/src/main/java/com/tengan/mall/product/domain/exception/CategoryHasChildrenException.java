package com.tengan.mall.product.domain.exception;

public class CategoryHasChildrenException extends RuntimeException {

    public CategoryHasChildrenException(Long id) {
        super("分類底下還有子節點，無法刪除: " + id);
    }
}

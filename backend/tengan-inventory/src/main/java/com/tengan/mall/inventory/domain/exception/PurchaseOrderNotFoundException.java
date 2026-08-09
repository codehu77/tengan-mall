package com.tengan.mall.inventory.domain.exception;

public class PurchaseOrderNotFoundException extends RuntimeException {

    public PurchaseOrderNotFoundException(Long id) {
        super("採購單不存在: id=" + id);
    }
}

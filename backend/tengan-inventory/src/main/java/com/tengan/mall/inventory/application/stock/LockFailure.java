package com.tengan.mall.inventory.application.stock;

public record LockFailure(Long skuId, LockFailureReason reason) {
}

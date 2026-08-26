package com.tengan.mall.order.application.port;

public record LockFailure(Long skuId, LockFailureReason reason) {
}

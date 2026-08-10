package com.tengan.mall.order.application.order;

public record ConfirmReceiptCommand(Long memberId, String orderSn) {
}

package com.tengan.mall.order.application.order;

public record CreateOrderCommand(Long memberId, String orderToken, ReceiverInfo receiverInfo,
        String paymentMethod, Long couponId, String remark) {
}

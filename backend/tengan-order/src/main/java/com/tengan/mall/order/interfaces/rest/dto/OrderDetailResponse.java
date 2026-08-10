package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDetailResponse(Long id, String orderSn, Long memberId, int status, String cancelReason,
        BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payAmount, String paymentMethod,
        Long couponId, String receiverName, String receiverPhone, String city, String district, String postalCode,
        String street, String remark, Instant receiptTime, Instant createdAt, List<OrderItemResponse> items) {
}

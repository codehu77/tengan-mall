package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDetail(Long id, String orderSn, Long memberId, int status, String cancelReason,
        BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payAmount, String paymentMethod,
        Long couponId, String receiverName, String receiverPhone, String city, String district, String postalCode,
        String street, String remark, Instant receiptTime, Instant createdAt, List<OrderItemDetail> items) {
}

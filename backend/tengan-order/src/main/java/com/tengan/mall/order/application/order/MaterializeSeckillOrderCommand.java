package com.tengan.mall.order.application.order;

import com.tengan.mall.order.domain.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;

public record MaterializeSeckillOrderCommand(String orderSn, Long memberId, String paymentMethod, Long couponId,
        Integer pointsUsed, BigDecimal pointsDiscountAmount, String receiverName, String receiverPhone, String city,
        String district, String postalCode, String street, String remark, BigDecimal totalAmount,
        BigDecimal discountAmount, BigDecimal payAmount, List<OrderItem> items) {
}

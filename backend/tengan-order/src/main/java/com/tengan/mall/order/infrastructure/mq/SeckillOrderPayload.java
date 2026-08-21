package com.tengan.mall.order.infrastructure.mq;

import com.tengan.mall.order.domain.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;

/**
 * order.seckill.order 訊息的 payload——tengan-order 自己當生產者也當消費者，直接複用 domain 的
 * OrderItem（本來就是不可變、無獨立生命週期的純資料），不用再包一層轉譯 DTO（見 Phase 9 規劃第 5 節）。
 */
public record SeckillOrderPayload(String orderSn, Long memberId, String paymentMethod, Long couponId,
        Integer pointsUsed, BigDecimal pointsDiscountAmount, String receiverName, String receiverPhone, String city,
        String district, String postalCode, String street, String remark, BigDecimal totalAmount,
        BigDecimal discountAmount, BigDecimal payAmount, List<OrderItem> items) {
}

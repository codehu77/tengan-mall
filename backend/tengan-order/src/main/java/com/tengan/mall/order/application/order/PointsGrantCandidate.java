package com.tengan.mall.order.application.order;

import java.math.BigDecimal;

public record PointsGrantCandidate(String orderSn, Long memberId, BigDecimal payAmount) {
}

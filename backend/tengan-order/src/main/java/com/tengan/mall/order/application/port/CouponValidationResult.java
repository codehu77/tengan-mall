package com.tengan.mall.order.application.port;

import java.math.BigDecimal;

public record CouponValidationResult(boolean valid, BigDecimal discountAmount) {
}

package com.tengan.mall.order.application.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmResult(String orderToken, List<ConfirmedItemView> items, BigDecimal totalAmount) {
}

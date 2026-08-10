package com.tengan.mall.order.application.order;

import java.math.BigDecimal;

public record CreateOrderResult(String orderSn, BigDecimal payAmount) {
}

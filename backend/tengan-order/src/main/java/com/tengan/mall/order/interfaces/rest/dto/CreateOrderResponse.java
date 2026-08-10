package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;

public record CreateOrderResponse(String orderSn, BigDecimal payAmount) {
}

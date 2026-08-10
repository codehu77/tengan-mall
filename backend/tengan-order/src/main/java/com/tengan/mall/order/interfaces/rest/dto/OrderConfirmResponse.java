package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmResponse(String orderToken, List<ConfirmedItemResponse> items, BigDecimal totalAmount) {
}

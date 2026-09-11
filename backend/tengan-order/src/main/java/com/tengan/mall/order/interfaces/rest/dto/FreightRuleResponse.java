package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;

public record FreightRuleResponse(BigDecimal freeShippingThreshold, BigDecimal shippingFee) {
}

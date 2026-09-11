package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;

public record FreightRuleResponse(BigDecimal freeShippingThreshold, BigDecimal shippingFee) {
}

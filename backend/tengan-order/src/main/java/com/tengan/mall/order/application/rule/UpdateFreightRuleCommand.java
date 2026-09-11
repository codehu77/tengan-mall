package com.tengan.mall.order.application.rule;

import java.math.BigDecimal;

public record UpdateFreightRuleCommand(BigDecimal freeShippingThreshold, BigDecimal shippingFee) {
}

package com.tengan.mall.order.application.rule;

import java.math.BigDecimal;

public record FreightRuleView(BigDecimal freeShippingThreshold, BigDecimal shippingFee) {
}

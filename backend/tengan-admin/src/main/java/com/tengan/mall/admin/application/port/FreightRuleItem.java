package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;

public record FreightRuleItem(BigDecimal freeShippingThreshold, BigDecimal shippingFee) {
}

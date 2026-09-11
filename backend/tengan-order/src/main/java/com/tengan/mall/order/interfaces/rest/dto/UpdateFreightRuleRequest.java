package com.tengan.mall.order.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record UpdateFreightRuleRequest(@NotNull @PositiveOrZero BigDecimal freeShippingThreshold,
        @NotNull @PositiveOrZero BigDecimal shippingFee) {
}

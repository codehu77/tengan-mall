package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateWalletRuleRequest(@NotNull BigDecimal cashbackRatePro, @NotNull BigDecimal cashbackRateProPlus,
        Integer monthlyCapPro, Integer monthlyCapProPlus, @Positive int pointExpiryDays,
        @Positive int gracePeriodMinutes, @NotNull BigDecimal pointValueRatio) {
}

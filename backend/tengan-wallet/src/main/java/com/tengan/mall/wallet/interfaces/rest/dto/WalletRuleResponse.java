package com.tengan.mall.wallet.interfaces.rest.dto;

import java.math.BigDecimal;

public record WalletRuleResponse(BigDecimal cashbackRatePro, BigDecimal cashbackRateProPlus, Integer monthlyCapPro,
        Integer monthlyCapProPlus, int pointExpiryDays, int gracePeriodMinutes, BigDecimal pointValueRatio) {
}

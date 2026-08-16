package com.tengan.mall.admin.infrastructure.wallet.dto;

import java.math.BigDecimal;

public record WalletRuleDto(BigDecimal cashbackRatePro, BigDecimal cashbackRateProPlus, Integer monthlyCapPro,
        Integer monthlyCapProPlus, int pointExpiryDays, int gracePeriodMinutes, BigDecimal pointValueRatio) {
}

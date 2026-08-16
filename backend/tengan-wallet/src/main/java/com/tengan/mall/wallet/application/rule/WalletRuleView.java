package com.tengan.mall.wallet.application.rule;

import java.math.BigDecimal;

public record WalletRuleView(BigDecimal cashbackRatePro, BigDecimal cashbackRateProPlus, Integer monthlyCapPro,
        Integer monthlyCapProPlus, int pointExpiryDays, int gracePeriodMinutes, BigDecimal pointValueRatio) {
}

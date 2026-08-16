package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;

public record WalletRuleItem(BigDecimal cashbackRatePro, BigDecimal cashbackRateProPlus, Integer monthlyCapPro,
        Integer monthlyCapProPlus, int pointExpiryDays, int gracePeriodMinutes, BigDecimal pointValueRatio) {
}

package com.tengan.mall.wallet.application.tier;

import java.math.BigDecimal;

public record GetMemberTierResult(String tier, String label, BigDecimal cashbackRate, Integer monthlyCap,
        int monthlyEarnedPoints) {
}

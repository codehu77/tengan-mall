package com.tengan.mall.wallet.interfaces.rest.dto;

import java.math.BigDecimal;

public record MemberTierResponse(String tier, String label, BigDecimal cashbackRate, Integer monthlyCap,
        int monthlyEarnedPoints) {
}

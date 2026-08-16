package com.tengan.mall.wallet.interfaces.rest.dto;

import java.util.List;

public record TierBenefitResponse(String tier, String label, String cashbackRateLabel, String monthlyCapLabel,
        List<String> perks) {
}

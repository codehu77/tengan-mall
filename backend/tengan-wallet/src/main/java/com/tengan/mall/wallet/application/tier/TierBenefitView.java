package com.tengan.mall.wallet.application.tier;

import java.util.List;

public record TierBenefitView(String tier, String label, String cashbackRateLabel, String monthlyCapLabel,
        List<String> perks) {
}

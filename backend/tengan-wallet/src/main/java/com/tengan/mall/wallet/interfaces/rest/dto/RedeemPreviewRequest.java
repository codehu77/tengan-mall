package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record RedeemPreviewRequest(@NotNull BigDecimal orderAmount, @Positive int points) {
}

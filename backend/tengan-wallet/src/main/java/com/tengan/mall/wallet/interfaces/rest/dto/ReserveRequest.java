package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ReserveRequest(@NotNull Long memberId, @NotBlank String orderSn, @NotNull BigDecimal payAmount) {
}

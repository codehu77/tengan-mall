package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** payAmount 只在 reserve 沒留下 PENDING 列時的補建路徑用得到。 */
public record EarnRequest(@NotNull Long memberId, @NotBlank String orderSn, BigDecimal payAmount) {
}

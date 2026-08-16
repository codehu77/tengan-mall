package com.tengan.mall.wallet.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RevertRequest(@NotNull Long memberId, @NotBlank String orderSn) {
}

package com.tengan.mall.order.infrastructure.wallet.dto;

import java.math.BigDecimal;

public record EarnRequestDto(Long memberId, String orderSn, BigDecimal payAmount) {
}

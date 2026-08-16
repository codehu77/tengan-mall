package com.tengan.mall.order.infrastructure.wallet.dto;

import java.math.BigDecimal;

public record ReserveRequestDto(Long memberId, String orderSn, BigDecimal payAmount) {
}

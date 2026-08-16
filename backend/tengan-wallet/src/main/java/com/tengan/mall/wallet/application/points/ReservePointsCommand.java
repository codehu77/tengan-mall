package com.tengan.mall.wallet.application.points;

import java.math.BigDecimal;

public record ReservePointsCommand(Long memberId, String orderSn, BigDecimal payAmount) {
}

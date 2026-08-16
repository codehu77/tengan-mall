package com.tengan.mall.wallet.application.points;

import java.math.BigDecimal;

/** payAmount 只在 reserve 沒有留下 PENDING 列時的補建路徑用得到，正常路徑忽略，直接用已保留的點數。 */
public record EarnPointsCommand(Long memberId, String orderSn, BigDecimal payAmount) {
}

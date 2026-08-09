package com.tengan.mall.coupon.application.membercoupon;

import java.util.List;

public record GrantCouponsResult(List<Long> succeededUserIds, List<Long> skippedUserIds) {
}

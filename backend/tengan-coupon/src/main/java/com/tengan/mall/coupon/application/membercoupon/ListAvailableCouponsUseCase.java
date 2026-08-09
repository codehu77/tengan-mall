package com.tengan.mall.coupon.application.membercoupon;

import java.math.BigDecimal;

public interface ListAvailableCouponsUseCase {

    ListMyCouponsResult list(Long userId, BigDecimal amount);
}

package com.tengan.mall.coupon.domain.repository;

import com.tengan.mall.coupon.domain.model.CouponOperLog;

public interface CouponOperLogRepository {

    CouponOperLog save(CouponOperLog operLog);
}

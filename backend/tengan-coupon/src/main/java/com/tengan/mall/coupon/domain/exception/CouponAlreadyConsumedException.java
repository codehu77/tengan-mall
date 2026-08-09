package com.tengan.mall.coupon.domain.exception;

public class CouponAlreadyConsumedException extends RuntimeException {

    public CouponAlreadyConsumedException(Long id) {
        super("優惠券已被使用，無法重複核銷: id=" + id);
    }
}

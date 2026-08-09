package com.tengan.mall.coupon.domain.exception;

public class CouponTemplateNotFoundException extends RuntimeException {

    public CouponTemplateNotFoundException(Long id) {
        super("優惠券模板不存在: id=" + id);
    }
}

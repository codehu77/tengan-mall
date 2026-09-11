package com.tengan.mall.coupon.domain.exception;

public class CouponTemplateNotGrantableException extends RuntimeException {

    public CouponTemplateNotGrantableException(Long id) {
        super("優惠券模板已下架或已過期，無法核發: id=" + id);
    }
}

package com.tengan.mall.order.domain.exception;

/** 優惠券驗證失敗（不屬於該會員/已使用/未達門檻）——重新核算金額，不信任前端送來的折扣值。 */
public class CouponNotApplicableException extends RuntimeException {

    public CouponNotApplicableException(Long couponId) {
        super("優惠券不可用: couponId=" + couponId);
    }
}

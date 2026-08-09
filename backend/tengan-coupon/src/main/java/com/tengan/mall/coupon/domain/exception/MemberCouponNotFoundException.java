package com.tengan.mall.coupon.domain.exception;

/** 找不到，或找到了但不屬於這個使用者——後者也回同一種例外，不洩漏存在性。 */
public class MemberCouponNotFoundException extends RuntimeException {

    public MemberCouponNotFoundException(Long id) {
        super("優惠券不存在: id=" + id);
    }
}

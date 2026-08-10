package com.tengan.mall.order.application.port;

import java.math.BigDecimal;

public interface CouponPort {

    /** 呼叫 tengan-coupon 的 POST /internal/coupons/validate——不信任前端送來的折扣金額，重新核算。 */
    CouponValidationResult validate(Long memberId, Long couponId, BigDecimal amount);

    /** 呼叫 POST /internal/coupons/{id}/consume，以 orderSn 冪等（重試視為成功）。 */
    void consume(Long couponId, String orderSn);

    /** 呼叫 POST /internal/coupons/{id}/revert，補償剛才的核銷（條件式 UPDATE，冪等 no-op 安全）。 */
    void revert(Long couponId, String orderSn);
}

package com.tengan.mall.coupon.application.membercoupon;

import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import org.springframework.stereotype.Service;

/** 只有 use_status=USED AND order_sn=? 才允許回滾，避免誤將已被其他訂單使用的券退回可用狀態；不符合條件一律 no-op。 */
@Service
public class RevertCouponService implements RevertCouponUseCase {

    private final MemberCouponRepository memberCouponRepository;

    public RevertCouponService(MemberCouponRepository memberCouponRepository) {
        this.memberCouponRepository = memberCouponRepository;
    }

    @Override
    public void revert(RevertCouponCommand command) {
        memberCouponRepository.revert(command.couponId(), command.orderSn());
    }
}

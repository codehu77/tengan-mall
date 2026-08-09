package com.tengan.mall.coupon.application.membercoupon;

import com.tengan.mall.coupon.domain.exception.CouponAlreadyConsumedException;
import com.tengan.mall.coupon.domain.exception.MemberCouponNotFoundException;
import com.tengan.mall.coupon.domain.model.CouponUseStatus;
import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 冪等：已經是同一個 orderSn 核銷過就直接視為成功（tengan-order 重試場景）；已被其他訂單用掉
 * 或條件式 UPDATE 搶輸（併發搶用）才是真的失敗。
 */
@Service
public class ConsumeCouponService implements ConsumeCouponUseCase {

    private final MemberCouponRepository memberCouponRepository;

    public ConsumeCouponService(MemberCouponRepository memberCouponRepository) {
        this.memberCouponRepository = memberCouponRepository;
    }

    @Override
    @Transactional
    public void consume(ConsumeCouponCommand command) {
        var coupon = memberCouponRepository.findById(command.couponId())
                .orElseThrow(() -> new MemberCouponNotFoundException(command.couponId()));

        if (coupon.getUseStatus() == CouponUseStatus.USED) {
            if (command.orderSn().equals(coupon.getOrderSn())) {
                return;
            }
            throw new CouponAlreadyConsumedException(command.couponId());
        }

        if (!memberCouponRepository.consume(command.couponId(), command.orderSn())) {
            throw new CouponAlreadyConsumedException(command.couponId());
        }
    }
}

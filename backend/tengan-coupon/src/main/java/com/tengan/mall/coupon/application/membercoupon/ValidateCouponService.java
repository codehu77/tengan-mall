package com.tengan.mall.coupon.application.membercoupon;

import com.tengan.mall.coupon.domain.exception.MemberCouponNotFoundException;
import com.tengan.mall.coupon.domain.model.CouponUseStatus;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class ValidateCouponService implements ValidateCouponUseCase {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    public ValidateCouponService(MemberCouponRepository memberCouponRepository,
            CouponTemplateRepository couponTemplateRepository) {
        this.memberCouponRepository = memberCouponRepository;
        this.couponTemplateRepository = couponTemplateRepository;
    }

    @Override
    public ValidateCouponResult validate(ValidateCouponCommand command) {
        var coupon = memberCouponRepository.findById(command.couponId())
                .filter(c -> c.getUserId().equals(command.userId()))
                .orElseThrow(() -> new MemberCouponNotFoundException(command.couponId()));

        if (coupon.getUseStatus() != CouponUseStatus.UNUSED) {
            return new ValidateCouponResult(false, BigDecimal.ZERO);
        }
        var template = couponTemplateRepository.findById(coupon.getTemplateId()).orElse(null);
        if (template == null || !template.isUsableFor(command.amount(), Instant.now())) {
            return new ValidateCouponResult(false, BigDecimal.ZERO);
        }
        return new ValidateCouponResult(true, template.calculateDiscount(command.amount()));
    }
}

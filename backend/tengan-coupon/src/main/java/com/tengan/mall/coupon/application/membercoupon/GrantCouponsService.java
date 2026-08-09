package com.tengan.mall.coupon.application.membercoupon;

import com.tengan.mall.coupon.domain.exception.CouponTemplateNotFoundException;
import com.tengan.mall.coupon.domain.model.CouponOperLog;
import com.tengan.mall.coupon.domain.model.MemberCoupon;
import com.tengan.mall.coupon.domain.repository.CouponOperLogRepository;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 對每個 userId 各自做一次「條件式 UPDATE issued_count+1」搶額度，搶到才 insert 一筆
 * member_coupon——額度滿了不是整批失敗，是那個 userId 被跳過（skipped），其餘照常核發成功。
 */
@Service
public class GrantCouponsService implements GrantCouponsUseCase {

    private final CouponTemplateRepository couponTemplateRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponOperLogRepository couponOperLogRepository;

    public GrantCouponsService(CouponTemplateRepository couponTemplateRepository,
            MemberCouponRepository memberCouponRepository, CouponOperLogRepository couponOperLogRepository) {
        this.couponTemplateRepository = couponTemplateRepository;
        this.memberCouponRepository = memberCouponRepository;
        this.couponOperLogRepository = couponOperLogRepository;
    }

    @Override
    @Transactional
    public GrantCouponsResult grant(GrantCouponsCommand command) {
        var template = couponTemplateRepository.findById(command.templateId())
                .orElseThrow(() -> new CouponTemplateNotFoundException(command.templateId()));

        List<Long> succeeded = new ArrayList<>();
        List<Long> skipped = new ArrayList<>();
        for (Long userId : command.userIds()) {
            if (couponTemplateRepository.tryIncrementIssuedCount(command.templateId())) {
                memberCouponRepository.save(MemberCoupon.grant(command.templateId(), userId));
                succeeded.add(userId);
            } else {
                skipped.add(userId);
            }
        }

        couponOperLogRepository.save(CouponOperLog.create(command.operator(), "grant", "grant",
                "核發優惠券模板 " + template.getName() + "（id=" + template.getId() + "）給 " + succeeded.size()
                        + " 位會員，跳過 " + skipped.size() + " 位（額度已滿）"));

        return new GrantCouponsResult(succeeded, skipped);
    }
}

package com.tengan.mall.coupon.application.template;

import com.tengan.mall.coupon.domain.exception.CouponTemplateNotFoundException;
import com.tengan.mall.coupon.domain.model.CouponOperLog;
import com.tengan.mall.coupon.domain.repository.CouponOperLogRepository;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTemplateService implements UpdateTemplateUseCase {

    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponOperLogRepository couponOperLogRepository;

    public UpdateTemplateService(CouponTemplateRepository couponTemplateRepository,
            CouponOperLogRepository couponOperLogRepository) {
        this.couponTemplateRepository = couponTemplateRepository;
        this.couponOperLogRepository = couponOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateTemplateCommand command) {
        var template = couponTemplateRepository.findById(command.id())
                .orElseThrow(() -> new CouponTemplateNotFoundException(command.id()));
        template.updateRule(command.name(), command.thresholdAmount(), command.discountAmount(),
                command.totalCount(), command.effectiveStart(), command.effectiveEnd());
        couponTemplateRepository.updateRule(template);

        couponOperLogRepository.save(CouponOperLog.create(command.operator(), "template", "update",
                "修改優惠券模板 " + template.getName() + "（id=" + template.getId() + "）"));
    }
}

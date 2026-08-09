package com.tengan.mall.coupon.application.template;

import com.tengan.mall.coupon.domain.model.CouponOperLog;
import com.tengan.mall.coupon.domain.model.CouponTemplate;
import com.tengan.mall.coupon.domain.repository.CouponOperLogRepository;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTemplateService implements CreateTemplateUseCase {

    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponOperLogRepository couponOperLogRepository;

    public CreateTemplateService(CouponTemplateRepository couponTemplateRepository,
            CouponOperLogRepository couponOperLogRepository) {
        this.couponTemplateRepository = couponTemplateRepository;
        this.couponOperLogRepository = couponOperLogRepository;
    }

    @Override
    @Transactional
    public CreateTemplateResult create(CreateTemplateCommand command) {
        CouponTemplate saved = couponTemplateRepository.save(CouponTemplate.create(command.name(),
                command.thresholdAmount(), command.discountAmount(), command.totalCount(),
                command.effectiveStart(), command.effectiveEnd()));

        couponOperLogRepository.save(CouponOperLog.create(command.operator(), "template", "create",
                "新增優惠券模板 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateTemplateResult(saved.getId());
    }
}

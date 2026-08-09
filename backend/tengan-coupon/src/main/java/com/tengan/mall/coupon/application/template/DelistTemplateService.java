package com.tengan.mall.coupon.application.template;

import com.tengan.mall.coupon.domain.exception.CouponTemplateNotFoundException;
import com.tengan.mall.coupon.domain.model.CouponOperLog;
import com.tengan.mall.coupon.domain.repository.CouponOperLogRepository;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DelistTemplateService implements DelistTemplateUseCase {

    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponOperLogRepository couponOperLogRepository;

    public DelistTemplateService(CouponTemplateRepository couponTemplateRepository,
            CouponOperLogRepository couponOperLogRepository) {
        this.couponTemplateRepository = couponTemplateRepository;
        this.couponOperLogRepository = couponOperLogRepository;
    }

    @Override
    @Transactional
    public void delist(DelistTemplateCommand command) {
        var template = couponTemplateRepository.findById(command.id())
                .orElseThrow(() -> new CouponTemplateNotFoundException(command.id()));
        couponTemplateRepository.delist(command.id());

        couponOperLogRepository.save(CouponOperLog.create(command.operator(), "template", "delist",
                "下架優惠券模板 " + template.getName() + "（id=" + template.getId() + "）"));
    }
}

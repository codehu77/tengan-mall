package com.tengan.mall.coupon.application.template;

import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class ListTemplatesService implements ListTemplatesUseCase {

    private final CouponTemplateRepository couponTemplateRepository;

    public ListTemplatesService(CouponTemplateRepository couponTemplateRepository) {
        this.couponTemplateRepository = couponTemplateRepository;
    }

    @Override
    public ListTemplatesResult list() {
        var items = couponTemplateRepository.findAll().stream()
                .map(t -> new TemplateSummary(t.getId(), t.getName(), t.getThresholdAmount(),
                        t.getDiscountAmount(), t.getTotalCount(), t.getIssuedCount(), t.getEffectiveStart(),
                        t.getEffectiveEnd(), t.getStatus().getValue()))
                .toList();
        return new ListTemplatesResult(items);
    }
}

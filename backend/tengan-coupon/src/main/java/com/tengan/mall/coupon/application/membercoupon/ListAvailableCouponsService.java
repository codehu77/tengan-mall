package com.tengan.mall.coupon.application.membercoupon;

import com.tengan.mall.coupon.domain.model.CouponTemplate;
import com.tengan.mall.coupon.domain.model.CouponUseStatus;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * skuIds 這個維度這次不做——優惠券店鋪通用，不限定商品範圍（見開發規劃「設計決策」），只依門檻金額
 * + 模板上架中 + 在有效期間內過濾。
 */
@Service
public class ListAvailableCouponsService implements ListAvailableCouponsUseCase {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    public ListAvailableCouponsService(MemberCouponRepository memberCouponRepository,
            CouponTemplateRepository couponTemplateRepository) {
        this.memberCouponRepository = memberCouponRepository;
        this.couponTemplateRepository = couponTemplateRepository;
    }

    @Override
    public ListMyCouponsResult list(Long userId, BigDecimal amount) {
        var unusedCoupons = memberCouponRepository.findByUserId(userId).stream()
                .filter(c -> c.getUseStatus() == CouponUseStatus.UNUSED)
                .toList();
        var templateIds = unusedCoupons.stream().map(c -> c.getTemplateId()).distinct().toList();
        Map<Long, CouponTemplate> templates = couponTemplateRepository.findByIds(templateIds).stream()
                .collect(Collectors.toMap(CouponTemplate::getId, Function.identity()));

        Instant now = Instant.now();
        var items = unusedCoupons.stream()
                .filter(c -> {
                    CouponTemplate template = templates.get(c.getTemplateId());
                    return template != null && template.isUsableFor(amount, now);
                })
                .map(c -> {
                    CouponTemplate template = templates.get(c.getTemplateId());
                    return new MyCouponView(c.getId(), template.getId(), template.getName(),
                            template.getThresholdAmount(), template.getDiscountAmount(),
                            c.getUseStatus().getValue(), c.getOrderSn(), c.getReceivedAt());
                })
                .toList();
        return new ListMyCouponsResult(items);
    }
}

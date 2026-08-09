package com.tengan.mall.coupon.application.membercoupon;

import com.tengan.mall.coupon.domain.model.CouponTemplate;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

/** 不 JOIN——先查 member_coupon 拿 templateId 清單，再批次查 coupon_template，應用層組裝（見資料庫設計規範）。 */
@Service
public class ListMyCouponsService implements ListMyCouponsUseCase {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    public ListMyCouponsService(MemberCouponRepository memberCouponRepository,
            CouponTemplateRepository couponTemplateRepository) {
        this.memberCouponRepository = memberCouponRepository;
        this.couponTemplateRepository = couponTemplateRepository;
    }

    @Override
    public ListMyCouponsResult list(Long userId) {
        var coupons = memberCouponRepository.findByUserId(userId);
        var templateIds = coupons.stream().map(c -> c.getTemplateId()).distinct().toList();
        Map<Long, CouponTemplate> templates = couponTemplateRepository.findByIds(templateIds).stream()
                .collect(java.util.stream.Collectors.toMap(CouponTemplate::getId, Function.identity()));

        var items = coupons.stream()
                .map(c -> {
                    CouponTemplate template = templates.get(c.getTemplateId());
                    return new MyCouponView(c.getId(), c.getTemplateId(),
                            template != null ? template.getName() : "",
                            template != null ? template.getThresholdAmount() : java.math.BigDecimal.ZERO,
                            template != null ? template.getDiscountAmount() : java.math.BigDecimal.ZERO,
                            c.getUseStatus().getValue(), c.getOrderSn(), c.getReceivedAt());
                })
                .toList();
        return new ListMyCouponsResult(items);
    }
}

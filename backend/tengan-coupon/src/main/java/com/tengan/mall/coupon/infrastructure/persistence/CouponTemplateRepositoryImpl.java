package com.tengan.mall.coupon.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.coupon.domain.model.CouponTemplate;
import com.tengan.mall.coupon.domain.repository.CouponTemplateRepository;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CouponTemplateRepositoryImpl implements CouponTemplateRepository {

    private final CouponTemplateMapper couponTemplateMapper;

    public CouponTemplateRepositoryImpl(CouponTemplateMapper couponTemplateMapper) {
        this.couponTemplateMapper = couponTemplateMapper;
    }

    @Override
    public CouponTemplate save(CouponTemplate template) {
        CouponTemplatePO po = toPO(template);
        couponTemplateMapper.insert(po);
        template.assignId(po.getId());
        return template;
    }

    @Override
    public void updateRule(CouponTemplate template) {
        couponTemplateMapper.updateRuleFields(toPO(template));
    }

    @Override
    public void delist(Long id) {
        couponTemplateMapper.delist(id);
    }

    @Override
    public Optional<CouponTemplate> findById(Long id) {
        return Optional.ofNullable(couponTemplateMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<CouponTemplate> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return couponTemplateMapper.selectBatchIds(ids).stream().map(this::toDomain).toList();
    }

    @Override
    public List<CouponTemplate> findAll() {
        return couponTemplateMapper.selectList(new LambdaQueryWrapper<CouponTemplatePO>()
                .orderByDesc(CouponTemplatePO::getCreatedAt)).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean tryIncrementIssuedCount(Long templateId) {
        return couponTemplateMapper.tryIncrementIssuedCount(templateId) > 0;
    }

    private CouponTemplatePO toPO(CouponTemplate template) {
        CouponTemplatePO po = new CouponTemplatePO();
        po.setId(template.getId());
        po.setName(template.getName());
        po.setThresholdAmount(template.getThresholdAmount());
        po.setDiscountAmount(template.getDiscountAmount());
        po.setTotalCount(template.getTotalCount());
        po.setIssuedCount(template.getIssuedCount());
        po.setEffectiveStart(template.getEffectiveStart().atZone(ZoneId.systemDefault()).toLocalDateTime());
        po.setEffectiveEnd(template.getEffectiveEnd().atZone(ZoneId.systemDefault()).toLocalDateTime());
        po.setStatus(template.getStatus());
        return po;
    }

    private CouponTemplate toDomain(CouponTemplatePO po) {
        return CouponTemplate.reconstitute(po.getId(), po.getName(), po.getThresholdAmount(),
                po.getDiscountAmount(), po.getTotalCount(), po.getIssuedCount(),
                po.getEffectiveStart().atZone(ZoneId.systemDefault()).toInstant(),
                po.getEffectiveEnd().atZone(ZoneId.systemDefault()).toInstant(), po.getStatus());
    }
}

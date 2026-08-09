package com.tengan.mall.coupon.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.coupon.domain.model.MemberCoupon;
import com.tengan.mall.coupon.domain.repository.MemberCouponRepository;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemberCouponRepositoryImpl implements MemberCouponRepository {

    private final MemberCouponMapper memberCouponMapper;

    public MemberCouponRepositoryImpl(MemberCouponMapper memberCouponMapper) {
        this.memberCouponMapper = memberCouponMapper;
    }

    @Override
    public MemberCoupon save(MemberCoupon coupon) {
        MemberCouponPO po = new MemberCouponPO();
        po.setTemplateId(coupon.getTemplateId());
        po.setUserId(coupon.getUserId());
        po.setUseStatus(coupon.getUseStatus());
        po.setOrderSn(coupon.getOrderSn());
        po.setReceivedAt(coupon.getReceivedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        memberCouponMapper.insert(po);
        coupon.assignId(po.getId());
        return coupon;
    }

    @Override
    public Optional<MemberCoupon> findById(Long id) {
        return Optional.ofNullable(memberCouponMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<MemberCoupon> findByUserId(Long userId) {
        return memberCouponMapper
                .selectList(new LambdaQueryWrapper<MemberCouponPO>().eq(MemberCouponPO::getUserId, userId)
                        .orderByDesc(MemberCouponPO::getReceivedAt))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean consume(Long id, String orderSn) {
        return memberCouponMapper.consume(id, orderSn) > 0;
    }

    @Override
    public boolean revert(Long id, String orderSn) {
        return memberCouponMapper.revert(id, orderSn) > 0;
    }

    private MemberCoupon toDomain(MemberCouponPO po) {
        return MemberCoupon.reconstitute(po.getId(), po.getTemplateId(), po.getUserId(), po.getUseStatus(),
                po.getOrderSn(), po.getReceivedAt().atZone(ZoneId.systemDefault()).toInstant());
    }
}

package com.tengan.mall.coupon.infrastructure.persistence;

import com.tengan.mall.coupon.domain.model.CouponOperLog;
import com.tengan.mall.coupon.domain.repository.CouponOperLogRepository;
import java.time.ZoneId;
import org.springframework.stereotype.Repository;

@Repository
public class CouponOperLogRepositoryImpl implements CouponOperLogRepository {

    private final CouponOperLogMapper couponOperLogMapper;

    public CouponOperLogRepositoryImpl(CouponOperLogMapper couponOperLogMapper) {
        this.couponOperLogMapper = couponOperLogMapper;
    }

    @Override
    public CouponOperLog save(CouponOperLog operLog) {
        CouponOperLogPO po = new CouponOperLogPO();
        po.setOperator(operLog.getOperator());
        po.setModule(operLog.getModule());
        po.setAction(operLog.getAction());
        po.setTargetDesc(operLog.getTargetDesc());
        po.setCreatedAt(operLog.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        couponOperLogMapper.insert(po);
        operLog.assignId(po.getId());
        return operLog;
    }
}

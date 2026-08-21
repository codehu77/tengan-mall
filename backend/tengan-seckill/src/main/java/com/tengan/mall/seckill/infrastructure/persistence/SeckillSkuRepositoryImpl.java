package com.tengan.mall.seckill.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SeckillSkuRepositoryImpl implements SeckillSkuRepository {

    private final SeckillSkuMapper mapper;

    public SeckillSkuRepositoryImpl(SeckillSkuMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public List<SeckillSku> replaceForActivity(Long activityId, List<SeckillSku> skus) {
        mapper.delete(new LambdaQueryWrapper<SeckillSkuPO>().eq(SeckillSkuPO::getActivityId, activityId));
        for (SeckillSku sku : skus) {
            SeckillSkuPO po = new SeckillSkuPO();
            po.setActivityId(sku.getActivityId());
            po.setSkuId(sku.getSkuId());
            po.setSeckillPrice(sku.getSeckillPrice());
            po.setSeckillCount(sku.getSeckillCount());
            po.setLimitPerUser(sku.getLimitPerUser());
            po.setSoldCount(sku.getSoldCount());
            po.setSettledAt(toLocalDateTime(sku.getSettledAt()));
            po.setCreatedAt(toLocalDateTime(sku.getCreatedAt()));
            mapper.insert(po);
            sku.assignId(po.getId());
        }
        return skus;
    }

    @Override
    public List<SeckillSku> findByActivityId(Long activityId) {
        return mapper.selectList(new LambdaQueryWrapper<SeckillSkuPO>().eq(SeckillSkuPO::getActivityId, activityId))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<SeckillSku> findUnsettledByActivityIds(List<Long> activityIds) {
        if (activityIds.isEmpty()) {
            return List.of();
        }
        return mapper
                .selectList(new LambdaQueryWrapper<SeckillSkuPO>().in(SeckillSkuPO::getActivityId, activityIds)
                        .isNull(SeckillSkuPO::getSettledAt))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean settle(Long id, int soldCount, Instant settledAt) {
        return mapper.settle(id, soldCount, toLocalDateTime(settledAt)) > 0;
    }

    private SeckillSku toDomain(SeckillSkuPO po) {
        return SeckillSku.reconstitute(po.getId(), po.getActivityId(), po.getSkuId(), po.getSeckillPrice(),
                po.getSeckillCount(), po.getLimitPerUser(), po.getSoldCount(), toInstant(po.getSettledAt()),
                toInstant(po.getCreatedAt()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}

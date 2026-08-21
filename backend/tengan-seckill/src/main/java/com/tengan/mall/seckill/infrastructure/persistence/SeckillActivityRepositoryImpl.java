package com.tengan.mall.seckill.infrastructure.persistence;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {

    private final SeckillActivityMapper mapper;

    public SeckillActivityRepositoryImpl(SeckillActivityMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SeckillActivity save(SeckillActivity activity) {
        SeckillActivityPO po = new SeckillActivityPO();
        po.setActivityType(activity.getActivityType());
        po.setStartTime(toLocalDateTime(activity.getStartTime()));
        po.setEndTime(toLocalDateTime(activity.getEndTime()));
        po.setStatus(activity.getStatus());
        po.setCreatedAt(toLocalDateTime(activity.getCreatedAt()));
        mapper.insert(po);
        activity.assignId(po.getId());
        return activity;
    }

    @Override
    public void update(SeckillActivity activity) {
        SeckillActivityPO po = new SeckillActivityPO();
        po.setId(activity.getId());
        po.setStatus(activity.getStatus());
        mapper.updateById(po);
    }

    @Override
    public Optional<SeckillActivity> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<SeckillActivity> findAll(int pageNum, int pageSize) {
        Page<SeckillActivityPO> page = mapper.selectPage(new Page<>(pageNum, pageSize), null);
        return page.getRecords().stream().map(this::toDomain).toList();
    }

    @Override
    public long countAll() {
        return mapper.selectCount(null);
    }

    @Override
    public List<SeckillActivity> findReadyToWarmUp(Instant now, Instant horizon) {
        return mapper.findReadyToWarmUp(toLocalDateTime(now), toLocalDateTime(horizon)).stream().map(this::toDomain)
                .toList();
    }

    @Override
    public List<SeckillActivity> findActiveEndedBefore(Instant cutoff) {
        return mapper.findActiveEndedBefore(toLocalDateTime(cutoff)).stream().map(this::toDomain).toList();
    }

    private SeckillActivity toDomain(SeckillActivityPO po) {
        return SeckillActivity.reconstitute(po.getId(), po.getActivityType(), toInstant(po.getStartTime()),
                toInstant(po.getEndTime()), po.getStatus(), toInstant(po.getCreatedAt()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}

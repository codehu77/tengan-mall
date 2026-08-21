package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import org.springframework.stereotype.Service;

@Service
public class GetActivityService implements GetActivityUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;

    public GetActivityService(SeckillActivityRepository activityRepository, SeckillSkuRepository skuRepository) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
    }

    @Override
    public ActivityDetailView get(Long activityId) {
        var activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        var skus = skuRepository.findByActivityId(activityId).stream()
                .map(s -> new SkuView(s.getId(), s.getSkuId(), s.getSeckillPrice(), s.getSeckillCount(),
                        s.getLimitPerUser(), s.getSoldCount(), s.getSettledAt()))
                .toList();
        return new ActivityDetailView(activity.getId(), activity.getActivityType(), activity.getStartTime(),
                activity.getEndTime(), activity.getStatus(), skus);
    }
}

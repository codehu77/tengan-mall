package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import org.springframework.stereotype.Service;

@Service
public class GetActivityService implements GetActivityUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final SeckillSessionRepository sessionRepository;

    public GetActivityService(SeckillActivityRepository activityRepository, SeckillSkuRepository skuRepository,
            SeckillSessionRepository sessionRepository) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public ActivityDetailView get(Long activityId) {
        var activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        var skus = skuRepository.findByActivityId(activityId).stream()
                .map(s -> new SkuView(s.getId(), s.getSkuId(), s.getSeckillPrice(), s.getSeckillCount(),
                        s.getLimitPerUser(), s.getSoldCount(), s.getSettledAt()))
                .toList();
        String sessionName = activity.getSessionId() == null ? null
                : sessionRepository.findById(activity.getSessionId()).map(SeckillSession::getName).orElse(null);
        return new ActivityDetailView(activity.getId(), activity.getActivityType(), activity.getStartTime(),
                activity.getEndTime(), activity.getSessionId(), activity.getActivityDate(), sessionName,
                activity.getStatus(), skus);
    }
}

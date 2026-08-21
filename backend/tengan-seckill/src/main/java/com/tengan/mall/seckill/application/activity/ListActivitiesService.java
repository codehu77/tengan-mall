package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class ListActivitiesService implements ListActivitiesUseCase {

    private final SeckillActivityRepository activityRepository;

    public ListActivitiesService(SeckillActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public ActivityPageResult list(int pageNum, int pageSize) {
        var items = activityRepository.findAll(pageNum, pageSize).stream()
                .map(a -> new ActivityView(a.getId(), a.getActivityType(), a.getStartTime(), a.getEndTime(),
                        a.getStatus()))
                .toList();
        return new ActivityPageResult(items, activityRepository.countAll());
    }
}

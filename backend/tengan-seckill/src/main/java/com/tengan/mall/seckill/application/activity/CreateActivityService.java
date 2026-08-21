package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateActivityService implements CreateActivityUseCase {

    private final SeckillActivityRepository activityRepository;

    public CreateActivityService(SeckillActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Long create(CreateActivityCommand command) {
        SeckillActivity activity = SeckillActivity.create(command.activityType(), command.startTime(),
                command.endTime());
        return activityRepository.save(activity).getId();
    }
}

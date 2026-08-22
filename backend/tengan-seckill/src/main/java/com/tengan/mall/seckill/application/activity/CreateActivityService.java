package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.exception.SessionNotFoundException;
import com.tengan.mall.seckill.domain.model.ActivityType;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateActivityService implements CreateActivityUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSessionRepository sessionRepository;

    public CreateActivityService(SeckillActivityRepository activityRepository,
            SeckillSessionRepository sessionRepository) {
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Long create(CreateActivityCommand command) {
        SeckillActivity activity = command.activityType() == ActivityType.FLASH_SALE
                ? SeckillActivity.createFlashSale(requireSession(command.sessionId()), command.activityDate())
                : SeckillActivity.createLaunch(command.startTime(), command.endTime());
        return activityRepository.save(activity).getId();
    }

    private SeckillSession requireSession(Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("FLASH_SALE 活動必須指定 sessionId");
        }
        return sessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException(sessionId));
    }
}

package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ListActivitiesService implements ListActivitiesUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSessionRepository sessionRepository;

    public ListActivitiesService(SeckillActivityRepository activityRepository,
            SeckillSessionRepository sessionRepository) {
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public ActivityPageResult list(int pageNum, int pageSize) {
        var activities = activityRepository.findAll(pageNum, pageSize);
        Map<Long, String> sessionNameCache = new HashMap<>();
        var items = activities.stream()
                .map(a -> new ActivityView(a.getId(), a.getActivityType(), a.getStartTime(), a.getEndTime(),
                        a.getSessionId(), a.getActivityDate(), sessionName(a.getSessionId(), sessionNameCache),
                        a.getStatus()))
                .toList();
        return new ActivityPageResult(items, activityRepository.countAll());
    }

    private String sessionName(Long sessionId, Map<Long, String> cache) {
        if (sessionId == null) {
            return null;
        }
        return cache.computeIfAbsent(sessionId,
                id -> sessionRepository.findById(id).map(SeckillSession::getName).orElse(null));
    }
}

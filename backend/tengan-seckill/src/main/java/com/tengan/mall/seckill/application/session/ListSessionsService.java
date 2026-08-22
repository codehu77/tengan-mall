package com.tengan.mall.seckill.application.session;

import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListSessionsService implements ListSessionsUseCase {

    private final SeckillSessionRepository sessionRepository;

    public ListSessionsService(SeckillSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<SessionView> list() {
        return sessionRepository.findAll().stream()
                .map(s -> new SessionView(s.getId(), s.getName(), s.getTimeOfDay(), s.getDurationMinutes(),
                        s.getSortOrder(), s.isEnabled()))
                .toList();
    }
}

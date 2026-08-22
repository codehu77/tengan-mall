package com.tengan.mall.seckill.application.session;

import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateSessionService implements CreateSessionUseCase {

    private final SeckillSessionRepository sessionRepository;

    public CreateSessionService(SeckillSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Long create(CreateSessionCommand command) {
        SeckillSession session = SeckillSession.create(command.name(), command.timeOfDay(),
                command.durationMinutes(), command.sortOrder(), command.enabled());
        return sessionRepository.save(session).getId();
    }
}

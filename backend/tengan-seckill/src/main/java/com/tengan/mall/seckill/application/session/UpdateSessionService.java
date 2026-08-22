package com.tengan.mall.seckill.application.session;

import com.tengan.mall.seckill.domain.exception.SessionNotFoundException;
import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateSessionService implements UpdateSessionUseCase {

    private final SeckillSessionRepository sessionRepository;

    public UpdateSessionService(SeckillSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void update(UpdateSessionCommand command) {
        SeckillSession session = sessionRepository.findById(command.id())
                .orElseThrow(() -> new SessionNotFoundException(command.id()));
        session.update(command.name(), command.timeOfDay(), command.durationMinutes(), command.sortOrder(),
                command.enabled());
        sessionRepository.update(session);
    }
}

package com.tengan.mall.seckill.application.session;

import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import org.springframework.stereotype.Service;

/** 不擋「已有活動引用這個場次」的刪除——沒有物理外鍵（見 db_design_conventions），
 * 既有活動保留自己算好的 startTime/endTime，只是往後查場次名稱時查不到，屬於已知的行為，不特別處理。 */
@Service
public class DeleteSessionService implements DeleteSessionUseCase {

    private final SeckillSessionRepository sessionRepository;

    public DeleteSessionService(SeckillSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void delete(Long id) {
        sessionRepository.delete(id);
    }
}

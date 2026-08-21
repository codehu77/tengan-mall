package com.tengan.mall.seckill.infrastructure.scheduler;

import com.tengan.mall.seckill.application.warmup.WarmUpActivitiesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 每日固定四個時間點觸發，讀 MySQL 寫 Redis（見規劃第 3 節）。 */
@Component
public class WarmUpScheduler {

    private static final Logger log = LoggerFactory.getLogger(WarmUpScheduler.class);

    private final WarmUpActivitiesUseCase warmUpActivitiesUseCase;

    public WarmUpScheduler(WarmUpActivitiesUseCase warmUpActivitiesUseCase) {
        this.warmUpActivitiesUseCase = warmUpActivitiesUseCase;
    }

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void warmUp() {
        int count = warmUpActivitiesUseCase.warmUp();
        log.info("秒殺場次預熱完成，處理 {} 個活動", count);
    }
}

package com.tengan.mall.seckill.infrastructure.scheduler;

import com.tengan.mall.seckill.application.settlement.SettleActivitiesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 固定間隔掃描已結束但還沒結算的活動（見規劃第 6 節）。間隔比預熱短很多，因為結算完成才是
 * 「模式復原」的觸發點，拖太久會讓已經賣完/結束的秒殺 SKU 遲遲無法恢復一般購買。 */
@Component
public class SettlementScheduler {

    private static final Logger log = LoggerFactory.getLogger(SettlementScheduler.class);

    private final SettleActivitiesUseCase settleActivitiesUseCase;

    public SettlementScheduler(SettleActivitiesUseCase settleActivitiesUseCase) {
        this.settleActivitiesUseCase = settleActivitiesUseCase;
    }

    @Scheduled(fixedDelayString = "${tengan.seckill.settlement-scan-interval-ms:300000}")
    public void settle() {
        int count = settleActivitiesUseCase.settle();
        if (count > 0) {
            log.info("秒殺場次結算完成，處理 {} 個活動", count);
        }
    }
}

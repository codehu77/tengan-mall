package com.tengan.mall.inventory.infrastructure.scheduler;

import com.tengan.mall.inventory.application.gate.SettleGatesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 固定間隔掃描已關閉但還沒結算的閘門，完全比照 tengan-seckill 的 SettlementScheduler，間隔比預熱短
 * 很多——結算完成才是「模式復原」的觸發點，拖太久會讓已經關閉的閘門遲遲無法恢復一般購買流程。 */
@Component
public class GateSettlementScheduler {

    private static final Logger log = LoggerFactory.getLogger(GateSettlementScheduler.class);

    private final SettleGatesUseCase settleGatesUseCase;

    public GateSettlementScheduler(SettleGatesUseCase settleGatesUseCase) {
        this.settleGatesUseCase = settleGatesUseCase;
    }

    @Scheduled(fixedDelayString = "${tengan.inventory.gate-settlement-scan-interval-ms:300000}")
    public void settle() {
        int count = settleGatesUseCase.settle();
        if (count > 0) {
            log.info("流量閘門結算完成，處理 {} 個 SKU", count);
        }
    }
}

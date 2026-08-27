package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.exception.InvalidGateCloseTimeException;
import com.tengan.mall.inventory.domain.exception.SkuLaunchConfigNotFoundException;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * 管理員在 SPU 列表頁「啟用/重設防超賣保護」用，一顆 SPU 底下每顆 SKU 各呼叫一次。沒有獨立的停用動作，
 * 「重設」的意思是：如果上一輪還在保護中（已預熱、尚未結算），先強制結算收尾——結算會把 Redis 實際
 * 賣出量同步回真實庫存、已購買會員的限購次數併回永久計數，再清空 Redis 的 semaphore/買家名單，
 * 所以先結算再重置不會有「已購買資格消失可以重複買」或「新快照庫存跟舊一輪銷售量對不上帳」的問題。
 * 不驗證庫存是否 >0——沒庫存也可以啟用（等同直接被 Redis 判定 0 剩餘），庫存不足只在呼叫端做軟性
 * 提醒，不在這裡擋。
 */
@Service
public class ConfigureGateService implements ConfigureGateUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final SettleGatesUseCase settleGatesUseCase;
    private final WarmUpGatesUseCase warmUpGatesUseCase;

    public ConfigureGateService(SkuLaunchConfigRepository skuLaunchConfigRepository,
            SettleGatesUseCase settleGatesUseCase, WarmUpGatesUseCase warmUpGatesUseCase) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.settleGatesUseCase = settleGatesUseCase;
        this.warmUpGatesUseCase = warmUpGatesUseCase;
    }

    @Override
    public void configure(Long skuId, LocalDateTime gateCloseTime) {
        var config = skuLaunchConfigRepository.findBySkuId(skuId)
                .orElseThrow(() -> new SkuLaunchConfigNotFoundException(skuId));

        LocalDateTime now = LocalDateTime.now();
        if (gateCloseTime == null || !gateCloseTime.isAfter(now)) {
            throw new InvalidGateCloseTimeException(skuId);
        }

        if (config.gateWarmedAt() != null && config.gateSettledAt() == null) {
            settleGatesUseCase.settleOne(skuId);
        }

        skuLaunchConfigRepository.configureGate(skuId, gateCloseTime);
        warmUpGatesUseCase.warmUpOne(skuId);
    }
}

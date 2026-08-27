package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.exception.SkuLaunchConfigNotFoundException;
import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 讀 MySQL（sku_launch_config：開啟閘門、還沒預熱、開賣時間快到的候選）→ 讀真實倉庫存快照 → 寫 Redis
 * （gate:stock:* 這把 RSemaphore）→ 條件式 UPDATE 標記 gate_warmed_at，避免下次排程重複預熱。完全
 * 比照 tengan-seckill 的 WarmUpActivitiesService，差別是保護的是真實倉庫存（
 * {@link WareSkuRepository#sumAvailableStock}）而不是另一份促銷配額。
 */
@Service
public class WarmUpGatesService implements WarmUpGatesUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final WareSkuRepository wareSkuRepository;
    private final GateQuotaAdapter gateQuotaAdapter;
    private final long warmUpHorizonHours;
    private final long settlementGraceMinutes;

    public WarmUpGatesService(SkuLaunchConfigRepository skuLaunchConfigRepository, WareSkuRepository wareSkuRepository,
            GateQuotaAdapter gateQuotaAdapter,
            @Value("${tengan.inventory.gate-warmup-horizon-hours:6}") long warmUpHorizonHours,
            @Value("${tengan.inventory.gate-settlement-grace-minutes:60}") long settlementGraceMinutes) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
        this.warmUpHorizonHours = warmUpHorizonHours;
        this.settlementGraceMinutes = settlementGraceMinutes;
    }

    @Override
    @Transactional
    public int warmUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime horizon = now.plusHours(warmUpHorizonHours);
        var candidates = skuLaunchConfigRepository.findReadyToWarmUp(now, horizon);

        int warmed = 0;
        for (SkuLaunchConfig config : candidates) {
            int protectedStock = wareSkuRepository.sumAvailableStock(config.skuId());
            Instant expireAt = toInstant(config.gateCloseTime()).plus(Duration.ofMinutes(settlementGraceMinutes));
            gateQuotaAdapter.warmUp(config.skuId(), protectedStock, expireAt);
            if (skuLaunchConfigRepository.markWarmed(config.skuId(), protectedStock, LocalDateTime.now())) {
                warmed++;
            }
        }
        return warmed;
    }

    /**
     * 「啟用/重設防超賣保護」用：不套用 findReadyToWarmUp 的候選篩選條件（不看 sale_start_time/horizon），
     * 呼叫端（ConfigureGateService）已經確保這是「剛被設定、要立刻生效」的動作，直接快照現在的庫存。
     */
    @Override
    public void warmUpOne(Long skuId) {
        SkuLaunchConfig config = skuLaunchConfigRepository.findBySkuId(skuId)
                .orElseThrow(() -> new SkuLaunchConfigNotFoundException(skuId));
        int protectedStock = wareSkuRepository.sumAvailableStock(skuId);
        Instant expireAt = toInstant(config.gateCloseTime()).plus(Duration.ofMinutes(settlementGraceMinutes));
        gateQuotaAdapter.warmUp(skuId, protectedStock, expireAt);
        skuLaunchConfigRepository.markWarmed(skuId, protectedStock, LocalDateTime.now());
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}

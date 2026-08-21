package com.tengan.mall.seckill.application.settlement;

import com.tengan.mall.seckill.application.port.InventoryPort;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 場次結束後把「Redis 配額實際賣掉多少」同步進真實庫存（見規劃第 6 節）。時序要求：這個動作
 * 必須在活動狀態轉回一般模式之前完成——本服務目前還沒有第 7 節的跨服務事件廣播，活動 status
 * 轉成 SETTLED 本身就是「模式復原」的唯一觸發點（Redis 端則是靠 TTL 被動過期，兩者各自獨立，
 * 但都要等這支結算方法真正跑完）。
 *
 * <p>某活動底下任一顆 SKU 結算失敗（呼叫 inventory 失敗、或 Redis 讀取失敗），整個活動這次先不
 * 標記 SETTLED，留給下一輪排程重試——已經成功結算的 SKU 有 {@code settled_at IS NULL} 條件式
 * UPDATE 擋著，重試不會重複扣庫存，是安全的，只是稍微浪費一次重複掃描（已知限制，跟 Phase 8.6
 * 排程式查帳的「重試直到成功」精神一致，但這裡沒有再往上疊一層獨立的查帳排程）。</p>
 */
@Service
public class SettleActivitiesService implements SettleActivitiesUseCase {

    private static final Logger log = LoggerFactory.getLogger(SettleActivitiesService.class);

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final QuotaGuardAdapter quotaGuardAdapter;
    private final InventoryPort inventoryPort;

    public SettleActivitiesService(SeckillActivityRepository activityRepository, SeckillSkuRepository skuRepository,
            QuotaGuardAdapter quotaGuardAdapter, InventoryPort inventoryPort) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.quotaGuardAdapter = quotaGuardAdapter;
        this.inventoryPort = inventoryPort;
    }

    @Override
    public int settle() {
        Instant now = Instant.now();
        List<SeckillActivity> candidates = activityRepository.findActiveEndedBefore(now);
        if (candidates.isEmpty()) {
            return 0;
        }

        List<Long> activityIds = candidates.stream().map(SeckillActivity::getId).toList();
        Map<Long, List<SeckillSku>> unsettledByActivity = skuRepository.findUnsettledByActivityIds(activityIds)
                .stream().collect(Collectors.groupingBy(SeckillSku::getActivityId));

        int settledCount = 0;
        for (SeckillActivity activity : candidates) {
            List<SeckillSku> skus = unsettledByActivity.getOrDefault(activity.getId(), List.of());
            boolean allSucceeded = true;
            for (SeckillSku sku : skus) {
                allSucceeded &= settleOne(sku);
            }
            if (allSucceeded) {
                activity.settle();
                activityRepository.update(activity);
                settledCount++;
            }
        }
        return settledCount;
    }

    private boolean settleOne(SeckillSku sku) {
        try {
            int availablePermits = quotaGuardAdapter.availablePermits(sku.getSkuId());
            int soldCount = Math.max(sku.getSeckillCount() - availablePermits, 0);
            if (soldCount > 0) {
                inventoryPort.seckillDeduct(sku.getSkuId(), soldCount);
            }
            boolean written = skuRepository.settle(sku.getId(), soldCount, Instant.now());
            if (!written) {
                log.warn("seckill_sku 結算條件式 UPDATE 沒有命中（可能已被其他排程結算過）: skuId={}", sku.getId());
            }
            return written;
        } catch (RuntimeException e) {
            log.error("秒殺結算失敗，留給下一輪排程重試: seckillSkuId={}, skuId={}", sku.getId(), sku.getSkuId(), e);
            return false;
        }
    }
}

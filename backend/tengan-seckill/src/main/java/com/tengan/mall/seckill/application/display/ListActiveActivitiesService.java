package com.tengan.mall.seckill.application.display;

import com.tengan.mall.seckill.application.port.ProductPort;
import com.tengan.mall.seckill.application.port.SkuInfo;
import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.ActivityType;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.model.SeckillSession;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSessionRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 商品名稱/圖片/原價這幾個 tengan-seckill 自己不存的欄位，向 tengan-product 批次補（見規劃文件第 1 節）。
 * FLASH_SALE 回傳「今天所有場次」（PUBLISHED+ACTIVE，供前台多場次分頁），LAUNCH 維持只回傳 ACTIVE
 * （不分場次，見場次機制規劃文件）。ACTIVE 場次的 remaining 讀 Redis 即時值；PUBLISHED（還沒預熱，
 * Redis semaphore 尚未建立）的 remaining 用 DB 靜態 seckillCount，避免誤判成 0。
 */
@Service
public class ListActiveActivitiesService implements ListActiveActivitiesUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final SeckillSessionRepository sessionRepository;
    private final ProductPort productPort;
    private final QuotaGuardAdapter quotaGuardAdapter;

    public ListActiveActivitiesService(SeckillActivityRepository activityRepository,
            SeckillSkuRepository skuRepository, SeckillSessionRepository sessionRepository, ProductPort productPort,
            QuotaGuardAdapter quotaGuardAdapter) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.sessionRepository = sessionRepository;
        this.productPort = productPort;
        this.quotaGuardAdapter = quotaGuardAdapter;
    }

    @Override
    public SeckillDisplayView list() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        List<SeckillActivity> flashSaleActivities = activityRepository.findFlashSaleSessionsOnDate(today);
        List<SeckillActivity> launchActivities = activityRepository.findActive().stream()
                .filter(a -> a.getActivityType() == ActivityType.LAUNCH).toList();

        List<SeckillActivity> allActivities = new ArrayList<>(flashSaleActivities);
        allActivities.addAll(launchActivities);
        if (allActivities.isEmpty()) {
            return new SeckillDisplayView(List.of(), List.of());
        }

        Map<Long, List<SeckillSku>> skusByActivity = allActivities.stream()
                .collect(Collectors.toMap(SeckillActivity::getId, a -> skuRepository.findByActivityId(a.getId())));
        List<Long> allSkuIds = skusByActivity.values().stream().flatMap(List::stream).map(SeckillSku::getSkuId)
                .distinct().toList();
        Map<Long, SkuInfo> skuInfoBySkuId = productPort.batchGet(allSkuIds).stream()
                .collect(Collectors.toMap(SkuInfo::skuId, Function.identity()));

        Map<Long, String> sessionNameCache = new java.util.HashMap<>();
        Instant now = Instant.now();

        List<FlashSaleSessionView> flashSaleSessions = flashSaleActivities.stream()
                // 過了結束時間但結算排程還沒處理掉的（最多幾分鐘的排程間隔差），不顯示——顯示「搶購中」或
                // 「準時開搶」都不對，乾脆先隱藏，等結算排程處理完自然從 findFlashSaleSessionsOnDate 消失。
                .filter(a -> now.isBefore(a.getEndTime()))
                .map(a -> {
                    // status=ACTIVE 只代表「已預熱、配額備妥」，不代表真的到了開賣時間——預熱排程會提前
                    // 最多 warmup-horizon-hours 執行，這裡要額外比對 startTime 才是「現在是不是真的搶購中」
                    // （真實 bug，見場次機制修正：之前只看 DB status，導致還沒開賣的場次也顯示「現正瘋搶」）。
                    boolean currentlyOpen = a.getStatus() == ActivityStatus.ACTIVE && !now.isBefore(a.getStartTime());
                    String displayStatus = currentlyOpen ? "ACTIVE" : "PUBLISHED";
                    boolean useRedisRemaining = a.getStatus() == ActivityStatus.ACTIVE;
                    return new FlashSaleSessionView(a.getId(), a.getSessionId(),
                            sessionName(a.getSessionId(), sessionNameCache), a.getStartTime(), a.getEndTime(),
                            displayStatus, toSkuViews(skusByActivity.get(a.getId()), skuInfoBySkuId, useRedisRemaining));
                })
                .filter(view -> !view.skus().isEmpty())
                .toList();

        List<LaunchView> launches = launchActivities.stream()
                .map(a -> new LaunchView(a.getId(), a.getStartTime(), a.getEndTime(),
                        toSkuViews(skusByActivity.get(a.getId()), skuInfoBySkuId, true)))
                .filter(view -> !view.skus().isEmpty())
                .toList();

        return new SeckillDisplayView(flashSaleSessions, launches);
    }

    private String sessionName(Long sessionId, Map<Long, String> cache) {
        if (sessionId == null) {
            return null;
        }
        return cache.computeIfAbsent(sessionId,
                id -> sessionRepository.findById(id).map(SeckillSession::getName).orElse(null));
    }

    private List<ActiveSkuView> toSkuViews(List<SeckillSku> skus, Map<Long, SkuInfo> skuInfoBySkuId,
            boolean useRedisRemaining) {
        return skus.stream()
                .map(sku -> {
                    SkuInfo info = skuInfoBySkuId.get(sku.getSkuId());
                    if (info == null) {
                        return null; // 商品已在 tengan-product 被刪除，不展示（不是結算/保留的關鍵路徑，安全忽略）
                    }
                    int remaining = useRedisRemaining ? quotaGuardAdapter.availablePermits(sku.getSkuId())
                            : sku.getSeckillCount();
                    return new ActiveSkuView(sku.getSkuId(), info.spuId(), info.name(), info.mainImage(),
                            info.price(), sku.getSeckillPrice(), sku.getLimitPerUser(), remaining);
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}

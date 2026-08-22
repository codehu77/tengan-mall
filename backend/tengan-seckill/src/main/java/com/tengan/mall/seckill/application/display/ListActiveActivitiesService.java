package com.tengan.mall.seckill.application.display;

import com.tengan.mall.seckill.application.port.ProductPort;
import com.tengan.mall.seckill.application.port.SkuInfo;
import com.tengan.mall.seckill.application.port.SpuInfo;
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
import java.util.HashMap;
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
 *
 * <p>一場活動底下的 SKU 依 spuId 分組成 {@link ActiveProductView}（一個商品一張卡，不是一個規格
 * 一張卡，見「秒殺改成綁 SPU」規劃文件）；remaining=0 的規格不會被濾掉，繼續回傳讓前端顯示成
 * 「已售完」，因為「後台故意設 0」跟「被搶完歸零」在系統裡是同一種狀態（見同一份規劃文件）。</p>
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
        List<Long> allSpuIds = skuInfoBySkuId.values().stream().map(SkuInfo::spuId).distinct().toList();
        Map<Long, SpuInfo> spuInfoBySpuId = productPort.batchGetSpu(allSpuIds).stream()
                .collect(Collectors.toMap(SpuInfo::spuId, Function.identity()));

        Map<Long, String> sessionNameCache = new HashMap<>();
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
                            displayStatus,
                            toProductViews(skusByActivity.get(a.getId()), skuInfoBySkuId, spuInfoBySpuId,
                                    useRedisRemaining));
                })
                .filter(view -> !view.products().isEmpty())
                .toList();

        List<LaunchView> launches = launchActivities.stream()
                .map(a -> new LaunchView(a.getId(), a.getStartTime(), a.getEndTime(),
                        toProductViews(skusByActivity.get(a.getId()), skuInfoBySkuId, spuInfoBySpuId, true)))
                .filter(view -> !view.products().isEmpty())
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

    private List<ActiveProductView> toProductViews(List<SeckillSku> skus, Map<Long, SkuInfo> skuInfoBySkuId,
            Map<Long, SpuInfo> spuInfoBySpuId, boolean useRedisRemaining) {
        Map<Long, List<SeckillSku>> skusBySpuId = skus.stream()
                .filter(sku -> skuInfoBySkuId.get(sku.getSkuId()) != null) // 商品已在 tengan-product 被刪除，安全忽略
                .collect(Collectors.groupingBy(sku -> skuInfoBySkuId.get(sku.getSkuId()).spuId()));

        List<ActiveProductView> products = new ArrayList<>();
        for (var entry : skusBySpuId.entrySet()) {
            Long spuId = entry.getKey();
            SpuInfo spuInfo = spuInfoBySpuId.get(spuId);
            if (spuInfo == null) {
                continue; // SPU 本身已被刪除，安全忽略
            }
            List<ActiveSkuView> skuViews = entry.getValue().stream()
                    .map(sku -> {
                        SkuInfo info = skuInfoBySkuId.get(sku.getSkuId());
                        // 後台把配額調降到低於已賣出數量時，Redis 差值調整後可能短暫為負（見 ReplaceProductSkusService
                        // 說明）——這裡夾在 0，不讓使用者看到「剩餘 -3 件」，實際能不能搶購已經由配額鎖本身擋住。
                        int remaining = useRedisRemaining ? Math.max(0, quotaGuardAdapter.availablePermits(sku.getSkuId()))
                                : sku.getSeckillCount();
                        return new ActiveSkuView(sku.getSkuId(), info.variantLabel(), info.price(),
                                sku.getSeckillPrice(), sku.getLimitPerUser(), remaining);
                    })
                    .toList();
            products.add(new ActiveProductView(spuId, spuInfo.name(), spuInfo.mainImage(), skuViews));
        }
        return products;
    }
}

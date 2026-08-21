package com.tengan.mall.seckill.application.display;

import com.tengan.mall.seckill.application.port.ProductPort;
import com.tengan.mall.seckill.application.port.SkuInfo;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 商品名稱/圖片/原價這幾個 tengan-seckill 自己不存的欄位，向 tengan-product 批次補（見規劃文件第 1 節）；
 * 即時剩餘量讀 {@link QuotaGuardAdapter#availablePermits}（跟結算排程用的是同一支方法，不是 DB 靜態值）。
 */
@Service
public class ListActiveActivitiesService implements ListActiveActivitiesUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final ProductPort productPort;
    private final QuotaGuardAdapter quotaGuardAdapter;

    public ListActiveActivitiesService(SeckillActivityRepository activityRepository,
            SeckillSkuRepository skuRepository, ProductPort productPort, QuotaGuardAdapter quotaGuardAdapter) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.productPort = productPort;
        this.quotaGuardAdapter = quotaGuardAdapter;
    }

    @Override
    public List<ActiveActivityView> list() {
        List<SeckillActivity> activities = activityRepository.findActive();
        if (activities.isEmpty()) {
            return List.of();
        }

        Map<Long, List<SeckillSku>> skusByActivity = activities.stream()
                .collect(Collectors.toMap(SeckillActivity::getId, a -> skuRepository.findByActivityId(a.getId())));

        List<Long> allSkuIds = skusByActivity.values().stream().flatMap(List::stream).map(SeckillSku::getSkuId)
                .distinct().toList();
        Map<Long, SkuInfo> skuInfoBySkuId = productPort.batchGet(allSkuIds).stream()
                .collect(Collectors.toMap(SkuInfo::skuId, Function.identity()));

        return activities.stream()
                .map(activity -> new ActiveActivityView(activity.getId(), activity.getActivityType(),
                        activity.getStartTime(), activity.getEndTime(),
                        toSkuViews(skusByActivity.get(activity.getId()), skuInfoBySkuId)))
                .filter(view -> !view.skus().isEmpty())
                .toList();
    }

    private List<ActiveSkuView> toSkuViews(List<SeckillSku> skus, Map<Long, SkuInfo> skuInfoBySkuId) {
        return skus.stream()
                .map(sku -> {
                    SkuInfo info = skuInfoBySkuId.get(sku.getSkuId());
                    if (info == null) {
                        return null; // 商品已在 tengan-product 被刪除，不展示（不是結算/保留的關鍵路徑，安全忽略）
                    }
                    int remaining = quotaGuardAdapter.availablePermits(sku.getSkuId());
                    return new ActiveSkuView(sku.getSkuId(), info.spuId(), info.name(), info.mainImage(),
                            info.price(), sku.getSeckillPrice(), sku.getLimitPerUser(), remaining);
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}

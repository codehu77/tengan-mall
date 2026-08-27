package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.repository.MemberSkuPurchaseCountRepository;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 閘門關閉後把「Redis 保護配額實際賣掉多少」同步進真實庫存、把每個會員這次閘門期間買的數量併回
 * 永久生效的限購計數，完全比照 tengan-seckill 的 SettleActivitiesService，差異：(1) 保護庫存是
 * 跨倉加總的，結算扣庫存要能跨多個倉扣，不像秒殺只認第一個倉；(2) 多一步把 Redis 限購計數併回
 * member_sku_purchase_count，因為 Phase B 的限購是永久生效的，不像秒殺場次結束後這個限購概念
 * 就沒意義了。
 *
 * <p>單一 sku 結算失敗（deduct 失敗、Redis 讀取失敗）只影響這一筆，不影響其他 sku，留給下一輪排程
 * 重試——已經成功結算的 sku 有 {@code gate_settled_at IS NULL} 條件式 UPDATE 擋著，重試不會重複扣
 * 庫存/重複併限購。</p>
 */
@Service
public class SettleGatesService implements SettleGatesUseCase {

    private static final Logger log = LoggerFactory.getLogger(SettleGatesService.class);

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final WareSkuRepository wareSkuRepository;
    private final MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository;
    private final GateQuotaAdapter gateQuotaAdapter;

    public SettleGatesService(SkuLaunchConfigRepository skuLaunchConfigRepository, WareSkuRepository wareSkuRepository,
            MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository, GateQuotaAdapter gateQuotaAdapter) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.memberSkuPurchaseCountRepository = memberSkuPurchaseCountRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
    }

    @Override
    public int settle() {
        List<SkuLaunchConfig> candidates = skuLaunchConfigRepository.findReadyToSettle(LocalDateTime.now());
        int settledCount = 0;
        for (SkuLaunchConfig config : candidates) {
            if (settleOne(config)) {
                settledCount++;
            }
        }
        return settledCount;
    }

    @Override
    public boolean settleOne(Long skuId) {
        SkuLaunchConfig config = skuLaunchConfigRepository.findBySkuId(skuId).orElse(null);
        if (config == null || config.gateWarmedAt() == null || config.gateSettledAt() != null) {
            return false;
        }
        return settleOne(config);
    }

    private boolean settleOne(SkuLaunchConfig config) {
        try {
            int protectedStock = config.gateProtectedStock() == null ? 0 : config.gateProtectedStock();
            int availablePermits = gateQuotaAdapter.availablePermits(config.skuId());
            int soldCount = Math.max(protectedStock - availablePermits, 0);

            if (soldCount > 0) {
                deductAcrossWarehouses(config.skuId(), soldCount);
            }

            for (Long memberId : gateQuotaAdapter.buyers(config.skuId())) {
                int purchased = gateQuotaAdapter.purchasedCount(config.skuId(), memberId);
                if (purchased > 0) {
                    memberSkuPurchaseCountRepository.addWithoutLimitCheck(memberId, config.skuId(), purchased);
                }
            }

            boolean written = skuLaunchConfigRepository.markSettled(config.skuId(), LocalDateTime.now());
            if (written) {
                gateQuotaAdapter.clear(config.skuId());
            } else {
                log.warn("流量閘門結算條件式 UPDATE 沒有命中（可能已被其他排程結算過）: skuId={}", config.skuId());
            }
            return written;
        } catch (RuntimeException e) {
            log.error("流量閘門結算失敗，留給下一輪排程重試: skuId={}", config.skuId(), e);
            return false;
        }
    }

    /**
     * 保護庫存是跨倉彙總的，結算扣庫存依序嘗試候選倉庫。閘門保護期間這個 sku 的 lock() 全部走
     * Redis 路徑（見 LockInventoryService），完全沒有其他請求動過 ware_sku，所以每個倉當下的
     * 可用庫存就是 warm-up 當時的原始分佈，直接讀來決定每個倉要扣多少即可，不用逐一嘗試。
     */
    private void deductAcrossWarehouses(Long skuId, int soldCount) {
        int remaining = soldCount;
        for (Long wareId : wareSkuRepository.findCandidateWareIds(skuId)) {
            if (remaining <= 0) {
                break;
            }
            int available = wareSkuRepository.availableStock(wareId, skuId);
            int deductCount = Math.min(remaining, available);
            if (deductCount > 0 && wareSkuRepository.deductStockOnly(wareId, skuId, deductCount)) {
                remaining -= deductCount;
            }
        }
        if (remaining > 0) {
            log.warn("流量閘門結算扣庫存時所有倉庫加總不足，缺口 {} 件: skuId={}", remaining, skuId);
        }
    }
}

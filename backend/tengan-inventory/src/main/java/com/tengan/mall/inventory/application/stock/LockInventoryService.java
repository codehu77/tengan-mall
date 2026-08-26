package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.model.WareOrderTask;
import com.tengan.mall.inventory.domain.model.WareOrderTaskDetail;
import com.tengan.mall.inventory.domain.repository.MemberSkuPurchaseCountRepository;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * lock 用 orderSn 是否已有對應 ware_order_task 判斷冪等；缺貨/尚未開賣/超過限購時已鎖成功的其他 sku
 * 會被補償釋放（庫存跟限購計數都要回滾），且不寫入 ware_order_task——下次同 orderSn 重試會用當下最新
 * 狀態重新嘗試，語意正確（見 .docs/微服務前台API待開發清單.md「開發細節：庫存鎖定/釋放/扣減」）。
 *
 * <p>Phase B：若該 sku 目前流量閘門正在保護中（{@link SkuLaunchConfig#isGateActive}），改走
 * {@link GateQuotaAdapter} 的 Redis 原子保留，完全跳過本來的 MySQL 條件式 UPDATE 路徑；產生的
 * {@link WareOrderTaskDetail} 用 {@code wareId=null} 當哨兵標記「這是閘門保留，不是真倉鎖定」，
 * 後續 release/deduct 都靠這個哨兵分流。</p>
 */
@Service
public class LockInventoryService implements LockInventoryUseCase {

    private final WareOrderTaskRepository wareOrderTaskRepository;
    private final WareSkuRepository wareSkuRepository;
    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository;
    private final GateQuotaAdapter gateQuotaAdapter;
    private final long settlementGraceMinutes;

    public LockInventoryService(WareOrderTaskRepository wareOrderTaskRepository, WareSkuRepository wareSkuRepository,
            SkuLaunchConfigRepository skuLaunchConfigRepository,
            MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository, GateQuotaAdapter gateQuotaAdapter,
            @Value("${tengan.inventory.gate-settlement-grace-minutes:60}") long settlementGraceMinutes) {
        this.wareOrderTaskRepository = wareOrderTaskRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.memberSkuPurchaseCountRepository = memberSkuPurchaseCountRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
        this.settlementGraceMinutes = settlementGraceMinutes;
    }

    @Override
    @Transactional
    public LockInventoryResult lock(LockInventoryCommand command) {
        if (wareOrderTaskRepository.findByOrderSn(command.orderSn()).isPresent()) {
            return new LockInventoryResult(true, List.of());
        }

        LocalDateTime now = LocalDateTime.now();
        List<WareOrderTaskDetail> lockedDetails = new ArrayList<>();
        List<LockFailure> failures = new ArrayList<>();
        // 這次呼叫中已經成功遞增的限購計數(僅一般 MySQL 路徑)，任何 item 失敗時要在同一個迴圈裡連同
        // 已鎖庫存一起補償扣回。閘門路徑的補償走 gateQuotaAdapter.release，不進這個 map。
        Map<Long, Integer> incrementedPurchaseCounts = new LinkedHashMap<>();

        for (LockInventoryItem item : command.items()) {
            SkuLaunchConfig launchConfig = skuLaunchConfigRepository.findBySkuId(item.skuId()).orElse(null);

            if (launchConfig != null && launchConfig.saleStartTime() != null
                    && now.isBefore(launchConfig.saleStartTime())) {
                failures.add(new LockFailure(item.skuId(), LockFailureReason.NOT_YET_ON_SALE));
                continue;
            }

            if (launchConfig != null && launchConfig.isGateActive(now)) {
                lockGateItem(command, item, launchConfig, lockedDetails, failures);
                continue;
            }

            Integer limit = launchConfig == null ? null : launchConfig.purchaseLimitPerUser();
            if (limit != null) {
                boolean withinLimit = memberSkuPurchaseCountRepository.tryIncrement(command.memberId(), item.skuId(),
                        item.count(), limit);
                if (!withinLimit) {
                    failures.add(new LockFailure(item.skuId(), LockFailureReason.PURCHASE_LIMIT_EXCEEDED));
                    continue;
                }
                incrementedPurchaseCounts.merge(item.skuId(), item.count(), Integer::sum);
            }

            Long wareId = tryLockAnyWarehouse(item.skuId(), item.count());
            if (wareId == null) {
                failures.add(new LockFailure(item.skuId(), LockFailureReason.OUT_OF_STOCK));
            } else {
                lockedDetails.add(new WareOrderTaskDetail(null, wareId, item.skuId(), item.count()));
            }
        }

        if (!failures.isEmpty()) {
            for (WareOrderTaskDetail detail : lockedDetails) {
                if (detail.wareId() == null) {
                    gateQuotaAdapter.release(detail.skuId(), command.memberId(), detail.skuCount());
                } else {
                    wareSkuRepository.release(detail.wareId(), detail.skuId(), detail.skuCount());
                }
            }
            incrementedPurchaseCounts.forEach((skuId, count) -> memberSkuPurchaseCountRepository
                    .decrement(command.memberId(), skuId, count));
            return new LockInventoryResult(false, failures);
        }

        wareOrderTaskRepository.save(WareOrderTask.lock(command.orderSn(), command.memberId(), lockedDetails));
        return new LockInventoryResult(true, List.of());
    }

    /** 閘門保護中的 item 改走 Redis 原子保留，完全不碰 ware_sku/member_sku_purchase_count。 */
    private void lockGateItem(LockInventoryCommand command, LockInventoryItem item, SkuLaunchConfig launchConfig,
            List<WareOrderTaskDetail> lockedDetails, List<LockFailure> failures) {
        Instant expireAt = toInstant(launchConfig.gateCloseTime()).plus(Duration.ofMinutes(settlementGraceMinutes));
        int limit = launchConfig.purchaseLimitPerUser() == null ? Integer.MAX_VALUE
                : launchConfig.purchaseLimitPerUser();
        var outcome = gateQuotaAdapter.tryReserve(item.skuId(), command.memberId(), item.count(), limit, expireAt);
        switch (outcome) {
            case SUCCESS -> lockedDetails.add(new WareOrderTaskDetail(null, null, item.skuId(), item.count()));
            case SOLD_OUT -> failures.add(new LockFailure(item.skuId(), LockFailureReason.OUT_OF_STOCK));
            case LIMIT_EXCEEDED -> failures.add(new LockFailure(item.skuId(), LockFailureReason.PURCHASE_LIMIT_EXCEEDED));
        }
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /** first-fit：依 ware_id 排序嘗試候選倉庫，第一個庫存足夠的就用該倉，不做跨倉拆單。 */
    private Long tryLockAnyWarehouse(Long skuId, int count) {
        for (Long wareId : wareSkuRepository.findCandidateWareIds(skuId)) {
            if (wareSkuRepository.tryLock(wareId, skuId, count)) {
                return wareId;
            }
        }
        return null;
    }
}

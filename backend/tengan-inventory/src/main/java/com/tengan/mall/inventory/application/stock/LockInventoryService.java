package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.model.WareOrderTask;
import com.tengan.mall.inventory.domain.model.WareOrderTaskDetail;
import com.tengan.mall.inventory.domain.repository.MemberSkuPurchaseCountRepository;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.domain.repository.WareOrderTaskRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * lock 用 orderSn 是否已有對應 ware_order_task 判斷冪等；缺貨/尚未開賣/超過限購時已鎖成功的其他 sku
 * 會被補償釋放（庫存跟限購計數都要回滾），且不寫入 ware_order_task——下次同 orderSn 重試會用當下最新
 * 狀態重新嘗試，語意正確（見 .docs/微服務前台API待開發清單.md「開發細節：庫存鎖定/釋放/扣減」）。
 */
@Service
public class LockInventoryService implements LockInventoryUseCase {

    private final WareOrderTaskRepository wareOrderTaskRepository;
    private final WareSkuRepository wareSkuRepository;
    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository;

    public LockInventoryService(WareOrderTaskRepository wareOrderTaskRepository, WareSkuRepository wareSkuRepository,
            SkuLaunchConfigRepository skuLaunchConfigRepository,
            MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository) {
        this.wareOrderTaskRepository = wareOrderTaskRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.memberSkuPurchaseCountRepository = memberSkuPurchaseCountRepository;
    }

    @Override
    @Transactional
    public LockInventoryResult lock(LockInventoryCommand command) {
        if (wareOrderTaskRepository.findByOrderSn(command.orderSn()).isPresent()) {
            return new LockInventoryResult(true, List.of());
        }

        List<WareOrderTaskDetail> lockedDetails = new ArrayList<>();
        List<LockFailure> failures = new ArrayList<>();
        // 這次呼叫中已經成功遞增的限購計數，任何 item 失敗時要在同一個迴圈裡連同已鎖庫存一起補償扣回。
        Map<Long, Integer> incrementedPurchaseCounts = new LinkedHashMap<>();

        for (LockInventoryItem item : command.items()) {
            SkuLaunchConfig launchConfig = skuLaunchConfigRepository.findBySkuId(item.skuId()).orElse(null);

            if (launchConfig != null && launchConfig.saleStartTime() != null
                    && LocalDateTime.now().isBefore(launchConfig.saleStartTime())) {
                failures.add(new LockFailure(item.skuId(), LockFailureReason.NOT_YET_ON_SALE));
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
                wareSkuRepository.release(detail.wareId(), detail.skuId(), detail.skuCount());
            }
            incrementedPurchaseCounts.forEach((skuId, count) -> memberSkuPurchaseCountRepository
                    .decrement(command.memberId(), skuId, count));
            return new LockInventoryResult(false, failures);
        }

        wareOrderTaskRepository.save(WareOrderTask.lock(command.orderSn(), lockedDetails));
        return new LockInventoryResult(true, List.of());
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
